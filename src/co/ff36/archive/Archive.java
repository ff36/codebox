package co.ff36.archive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A basic archived object metadata.
 *
 * Created by tarka on 28/03/2016.
 */
public class Archive {

    private static final String DATE_FORMAT = "MM-dd-yyyy";
    private String companyName;
    private String accountName;
    private String orderNumber;
    private Date archivedDate;
    private long size;
    private String key;

    public Archive(String key, long size) throws ParseException {
        String[] split = key.replace(".zip", "").split("#");
        this.companyName = split[0].replace("_", " ");
        this.accountName = split[1].replace("_", " ");
        this.orderNumber = split[2].replace("_", " ");
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        this.archivedDate = formatter.parse(split[3]);
        this.size = size;
        this.key = key;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getArchivedDate() {
        return archivedDate;
    }

    public void setArchivedDate(Date archivedDate) {
        this.archivedDate = archivedDate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static String toKey(String companyName, String accountName, String orderNumber) {

        return String.format(
                "%s#%s#%s#%s.zip",
                companyName.toLowerCase().replace("#", "").replace(" ", "_"),
                accountName.toLowerCase().replace("#", "").replace(" ", "_"),
                orderNumber.toLowerCase().replace("#", "").replace(" ", "_"),
                new SimpleDateFormat(DATE_FORMAT).format(new Date()));
    }
}
