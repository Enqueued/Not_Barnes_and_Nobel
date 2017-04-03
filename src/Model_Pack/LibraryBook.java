package Model_Pack;

/**
 * Created by ultimaq on 3/19/17.
 * going to be an association class that will house a single record from the library_book junction table and provides
 * the domain model representation of the relationship between a library and its books and how many copies of each book
 * the library has in its inventory.
 */
public class LibraryBook {

    private Book book;
    private int quantity;
    private boolean newRecord = true;


}
