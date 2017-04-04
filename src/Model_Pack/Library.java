package Model_Pack;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by ultimaq on 4/1/17.
 */
public class Library {
    private int id;
    private String libraryName;
    private List<LibraryBook> books;
    private LocalDateTime lastModified;

    private Library(){
        this.id = 0;
        this.libraryName = "";
        this.books=null;
        this.lastModified = null;
    }

    public Library(int id, String libraryName, List<LibraryBook> books, LocalDateTime lastModified){
        this.id = id;
        this.libraryName=libraryName;
        this.books=books;
        this.lastModified=lastModified;
    }

    //obligitory tostring
    public String toString(){
        return this.libraryName;
    }

    //getters and setters
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getLibraryName(){
        return this.libraryName;
    }

    public void setLibraryName(String libraryName){
        this.libraryName=libraryName;
    }

    public LocalDateTime getLastModified(){
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified){
        this.lastModified=lastModified;
    }

    public List<LibraryBook> getBooks() {
        return books;
    }

    public void setBooks(List<LibraryBook> books) {
        this.books = books;
    }
}
