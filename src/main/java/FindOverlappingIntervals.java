class Interval{
    private final int start, end;
    public Interval(int a, int b) {
        this.start = a; this.end = b;
    }
    public boolean contains(int p) {
        int x1 = p - start, y1 = p - end;
        return ((x1 <= 0) && (y1 >= 0)) || ((x1 >= 0) && (y1 <= 0));
    }
    public boolean overlaps(Interval interval) {
        return interval.contains(this.start) || interval.contains(this.end);
    }

    @Override
    public String toString() {
        return "(" + this.start + "," + this.end + ")";
    }
}

public class FindOverlappingIntervals {
    public static void main(String[] args) {
        Interval interval1 = new Interval(2, 5);
        Interval interval2 = new Interval(4, 6);
        if(interval1.overlaps(interval2)) {
            System.out.println("intervals test" + interval1 + ", " + interval2);
            System.out.println("Intervals overlap");
        }
        interval1 = new Interval(2, 5);
        interval2 = new Interval(5, 10);
        if(interval1.overlaps(interval2)) {
            System.out.println("intervals test" + interval1 + ", " + interval2);
            System.out.println("Intervals overlap");
        }
        interval1 = new Interval(2, 5);
        interval2 = new Interval(7, 10);
        System.out.println("intervals test" + interval1 + ", " + interval2);
        if(interval1.overlaps(interval2)) {
            System.out.println("Intervals overlap");
        } else {
            System.out.println("intervals don't overlap");
        }
        interval1 = new Interval(5, 2);
        interval2 = new Interval(4, 10);
        System.out.println("intervals test" + interval1 + ", " + interval2);
        if(interval1.overlaps(interval2)) {
            System.out.println("Intervals overlap");
        } else {
            System.out.println("intervals don't overlap");
        }

    }
}
