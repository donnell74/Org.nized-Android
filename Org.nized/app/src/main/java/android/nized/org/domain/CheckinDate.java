package android.nized.org.domain;

import java.util.Date;

/**
 * Created by greg on 1/7/15.
 */
public class CheckinDate {
    private Date date_scanned;

    public CheckinDate(Date date_scanned) {
        this.date_scanned = date_scanned;
    }

    public Date getDate_scanned() {
        return date_scanned;
    }

    public void setDate_scanned(Date date_scanned) {
        this.date_scanned = date_scanned;
    }
}
