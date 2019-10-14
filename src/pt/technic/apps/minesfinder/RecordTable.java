package pt.technic.apps.minesfinder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Gabriel Massadas
 */
public class RecordTable implements Serializable {

    private transient final int MAX_CHAR = 10;
    
    private String name;
    private long score;

    private transient ArrayList<RecordTableListener> listeners;

    public RecordTable() {
        name = "Anonymous";
        score = 9999999;
        listeners = new ArrayList<>();
    }

    public String getName() {
        return name.substring(0, Math.min(MAX_CHAR, name.length()));
    }

    public long getScore() {
        return score;
    }

    public void setRecord(String name, long score) {
        if (score < this.score) {
            this.name = name;
            this.score = score;
            notifyRecordTableUpdated();
        }
    }

    public void addRecordTableListener(RecordTableListener list) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(list);
    }

    public void removeRecordTableListener(RecordTableListener list) {
        if (listeners != null) {
            listeners.remove(list);
        }
    }

    private void notifyRecordTableUpdated() {
        if (listeners != null) {
            for (RecordTableListener list : listeners) {
                list.recordUpdated(this);
            }
        }
    }
}
