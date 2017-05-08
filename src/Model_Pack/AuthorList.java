package Model_Pack;

import Gate_Pack.AuthorTableGateway;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by ultimaq on 5/7/17.
 */
public class AuthorList extends Task<Void> {
    List<Author> list = new ArrayList<Author>();
    private static Logger logger = LogManager.getLogger();
    MyRedisListener myListener = null;
    ObservableList<Author> items;
    AuthorTableGateway ATG;

    public AuthorList (ObservableList<Author> items){
        this.items = items;
    }

    @Override
    protected Void call() throws Exception {
        myListener = new MyRedisListener();
        myListener.subscribeAndBlock("Books");
        return null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning){
        boolean ret = super.cancel(mayInterruptIfRunning);
        if(myListener != null){
            myListener.unsubscribe();
        }
        return ret;
    }

    class MyRedisListener extends JedisPubSub {
        private final Logger logger2 = LogManager.getLogger(MyRedisListener.class);
        private MysqlDataSource ds = null;
        Jedis jedis = null;
        public MyRedisListener() throws IOException {
            Properties props = new Properties();
            FileInputStream file = null;
            file = new FileInputStream("./src/db.properties");
            props.load(file);
            file.close();
            jedis = new Jedis("easel2.fulgentcorp.com");
            jedis.auth("pd6BvDKAEMXhxwUg");
            jedis.select(0);
        }

        public void subscribeAndBlock(String channel){
            jedis.subscribe(this, channel);
        }

        @Override
        public void onMessage(String channel, String message){
            items.clear();
            try{
                ATG = new AuthorTableGateway();
                list = ATG.getAuthors();
                for(Author auth : list){
                    items.add(auth);
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
            updateMessage(message);
        }
    }
}
