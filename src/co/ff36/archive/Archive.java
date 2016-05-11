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
    private static final String EMPTY = "empty";
    private String companyName;
    private String accountName;
    private String orderNumber;
    private String extra;
    private Date archivedDate;
    private long size;
    private String key;

    public Archive(String key, long size) throws ParseException {
        String[] split = key.replace(".zip", "").split("#");
        this.companyName = split[0].replace("_", " ").replace("|", "/");
        this.accountName = split[1].replace("_", " ").replace("|", "/");
        this.orderNumber = split[2].replace("_", " ").replace("|", "/");
        this.extra = split[3].replace("_", " ").replace("|", "/");
        if (extra.equals(EMPTY)) this.extra = "";
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        this.archivedDate = formatter.parse(split[4]);
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

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra.substring(0, Math.min(extra.length(), 50));;
    }

    /**
     * Generate an S3 file key from the archive data
     * @param companyName The company name
     * @param accountName The account name
     * @param orderNumber The order number
     * @param extra The extra data
     * @return The S3 file key
     */
    public static String toKey(String companyName, String accountName, String orderNumber, String extra) {
        if (extra.isEmpty()) extra = EMPTY;
        return String.format(
                "%s#%s#%s#%s#%s.zip",
                companyName.toLowerCase().replace("#", "").replace(" ", "_").replace("/", "|"),
                accountName.toLowerCase().replace("#", "").replace(" ", "_").replace("/", "|"),
                orderNumber.toLowerCase().replace("#", "").replace(" ", "_").replace("/", "|"),
                extra.toLowerCase().replace("#", "").replace(" ", "_").replace("/", "|"),
                new SimpleDateFormat(DATE_FORMAT).format(new Date()));
    }
}