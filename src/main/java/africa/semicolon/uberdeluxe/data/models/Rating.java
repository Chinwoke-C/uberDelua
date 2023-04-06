package africa.semicolon.uberdeluxe.data.models;

public enum Rating {
    BAD(1),
    SATISFACTORY(2),
    GOOD(3),
    EXCELLENT(4);
    private int rating;
    private int getRating(){
        return rating;
    }

    Rating(int rating) {
        this.rating = rating;
    }
}
