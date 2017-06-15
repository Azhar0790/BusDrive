package busdriver.com.vidriver.module;

/**
 * Created by Sarps on 2/8/2017.
 */
public class Message_item {
    String msg,date;

    public Message_item(String msg, String date) {
        this.msg = msg;
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
