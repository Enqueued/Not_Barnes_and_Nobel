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

    public LibraryBook(){
        book = new Book();
        quantity = 0;
        newRecord = true;
    }

    //record defaults to false
    public LibraryBook(int quantity, Book book, boolean record){
        this.book = book;
        this.quantity = quantity;
        this.newRecord = record;
    }

    @Override
    public String toString(){
        return "Book: " + book.getTitle() + " (" + quantity +") ";
    }

    //Getters - Setters
    public Book getBook(){
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isNewRecord() {
        return newRecord;
    }

    public void setNewRecord(boolean newRecord) {
        this.newRecord = newRecord;
    }
}
