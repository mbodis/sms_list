package sk.svb.sms_todo_list.logic;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by mbodis on 9/14/15.
 */
public class Note extends SugarRecord<Note> {
    @Ignore
    public static final int TYPE_NOTE = 0;
    @Ignore
    public static final int TYPE_ADAPTER_TITLE = 1;

    private String title;
    private String message;
    private String date;
    private boolean deleted = false;

    @Ignore
    public int adapterType = TYPE_NOTE;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
