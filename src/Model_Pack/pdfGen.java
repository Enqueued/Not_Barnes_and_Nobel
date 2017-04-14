package Model_Pack;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by ultimaq on 4/13/17.
 */
public class pdfGen {
	private static Logger logger = LogManager.getLogger();
	public pdfGen(){
	}

	public void createPDF(Library l)throws IOException, DocumentException{
		Document document = new Document();
        //set to Landscape size 11 width x 8.5 height
        document.setPageSize(PageSize.LETTER.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(l.getLibraryName() + ".pdf"));
        document.open();
        //display title background rectangle
        PdfContentByte canvas = writer.getDirectContent();
        Rectangle rect = new Rectangle(36, document.getPageSize().getHeight() - 36
        		, document.getPageSize().getWidth() - 36, document.getPageSize().getHeight() - 76);
        rect.setBorder(Rectangle.BOX);
        rect.setBorderWidth(2);
        rect.setBackgroundColor(BaseColor.LIGHT_GRAY);
        canvas.rectangle(rect);

        //overlay title text on top of rectangle
        ColumnText ct = new ColumnText(canvas);
        Rectangle rect2 = new Rectangle(36, document.getPageSize().getHeight() - 29
        		, document.getPageSize().getWidth() - 36, 40);
        ct.setSimpleColumn(rect2);
        Font f = new Font(FontFamily.HELVETICA, 25.0f, Font.BOLD, BaseColor.BLACK);
        Chunk c = new Chunk(l.getLibraryName() + " Report", f);
        ct.addElement(new Paragraph(c));
        ct.go();

        //add 4 paragraphs
        f = new Font(FontFamily.TIMES_ROMAN, 16.0f);
       // c = new Chunk("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer sit amet nisi ac libero elementum molestie. Curabitur orci arcu, bibendum et auctor vitae, placerat nec risus. Aenean aliquam augue eget nunc blandit, eget pretium lorem dapibus. Suspendisse id odio orci. Aenean ligula dolor, fringilla vel tempus a, ornare a ipsum. Phasellus interdum sagittis dignissim. Nulla at augue ac ligula cursus semper. Suspendisse non aliquam odio. Aliquam consectetur imperdiet auctor. Vestibulum et placerat felis. Aliquam suscipit viverra turpis, at luctus nisl posuere id. Quisque vestibulum ornare massa non malesuada.", f);

        /*
         * bump the first paragraph down a bit so it doesn't display UNDER the title rectangle
         */
        document.add(new Paragraph("\n\n\n"));
        c = new Chunk("Library Id: " + l.getId());
        Paragraph p = new Paragraph(c);
        p.setFirstLineIndent(50);
        document.add(p);
        c = new Chunk("Last Modified: " + l.getLastModified());
        p = new Paragraph(c);
        p.setFirstLineIndent(50);
        document.add(p);
        document.add(new Paragraph("\n"));
        PdfPTable table = new PdfPTable(4);
        table.addCell("Book's Title");
        table.addCell("Author's Full Name");
        table.addCell("Book's Publisher");
		table.addCell("Quantity");

		table.completeRow();
        List<LibraryBook> b = new ArrayList<LibraryBook>();
        b = l.getBooks();
        for(int x = 0; x < b.size(); x++){
        	logger.info("gfhgh");
        		logger.debug("in loop");
        		table.addCell(b.get(x).getBook().getTitle());
        		table.addCell(b.get(x).getBook().getAuthor().toString());
        		table.addCell(b.get(x).getBook().getPublisher()+ "");
        		table.addCell(b.get(x).getQuantity() + "");
        		logger.debug(b.get(x).getBook().getTitle());
        		table.completeRow();
        }
        document.add(table);

        document.close();
	}
}
