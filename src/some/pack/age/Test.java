package some.pack.age;

import some.pack.age.models.AxisAlignedBB;
import some.pack.age.models.Point;
import some.pack.age.models.PosPoint;
import some.pack.age.models.SliderPoint;
import some.pack.age.test.ImageGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author DarkSeraphim.
 */
public class Test
{

    private static List<Point> points = new ArrayList<>();

    private static BiPredicate<Point, Point> doOverlap;

    private static Counter counter = new Counter();

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        String model = scanner.nextLine().split(": ")[1];
        Function<Scanner, Point> pointParser;
        switch (model)
        {
            case "2pos":
                pointParser = Test::get2pos;
                break;
            case "4pos":
                pointParser = Test::get4pos;
                break;
            case "1slider":
                pointParser = Test::getSliderPoint;
                break;
            default:
                throw new RuntimeException("Illegal placement model");
        }
        int width = Integer.parseInt(scanner.nextLine().split(": ")[1]);
        int height = Integer.parseInt(scanner.nextLine().split(": ")[1]);
        Test.doOverlap = (point, other) -> {
            AxisAlignedBB a = point.getAABB(width, height);
            AxisAlignedBB b = other.getAABB(width, height);
            return a.overlaps(b);
        };
        int pointsToParse = Integer.parseInt(scanner.nextLine().split(": ")[1]);
        String found = scanner.nextLine();

        for (int i = 0; i < pointsToParse; i++)
        {
            points.add(pointParser.apply(scanner));
        }

        points.stream()
              .filter(Point::isValid)
              .forEach(Test::checkPoint);
        System.out.println("Overlaps counted: " + counter);
        System.out.println("Do you want to generate an image? [y/n]:\t");
        char c = scanner.next().charAt(0);
        if (c == 'y')
        {
            ImageGenerator.generateImage(points, width, height);
        }
    }

    private static void checkPoint(final Point point)
    {

        Predicate<Point> notEquals = other -> !other.equals(point);

        Consumer<Point> report = other -> System.out.printf("Label {%s} overlaps with label {%s}\r\n", other.toString(), point.toString());
        report = report.andThen(p -> counter.increment());
        Predicate<Point> doOverlap = other -> Test.doOverlap.test(point, other);
        points.stream()
              .filter(Point::isValid)
              .filter(notEquals)
              .filter(doOverlap)
              .forEach(report);
    }

    private static Point get2pos(Scanner scanner)
    {
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        String label = scanner.next();
        return new PosPoint(x, y, LabelPosition.fromString(label), false);
    }

    private static Point get4pos(Scanner scanner)
    {
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        String label = scanner.next();
        return new PosPoint(x, y, LabelPosition.fromString(label), true);
    }

    private static Point getSliderPoint(Scanner scanner)
    {
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        String slider = scanner.next();
        Optional<Float> f;
        try
        {
            f = Optional.of(Float.parseFloat(slider));
        }
        catch (NumberFormatException ex)
        {
            f = Optional.empty();
        }
        return new SliderPoint(x, y, f);
    }

    private static class Counter
    {
        private int c = 0;

        public void increment()
        {
            this.c++;
        }

        @Override
        public String toString()
        {
            return String.valueOf(this.c);
        }
    }
}
