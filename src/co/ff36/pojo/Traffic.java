package co.ff36.pojo;

import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Upload;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tarka on 15/05/2016.
 */
public class Traffic extends Task<Void> {

    public enum Type {
        Upload, Download
    }

    public enum State {
        Current, Success, Fail
    }

    private UUID id;
    private Type type;
    private Upload upload;
    private Download download;
    private State status;
    private boolean aborted;
    private SimpleStringProperty timestamp;

    public Traffic(Type type, Upload upload) {
        this.type = type;
        this.upload = upload;
        init();
    }

    public Traffic(Type type, Download download) {
        this.type = type;
        this.download = download;
        init();
    }

    public UUID getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Upload getUpload() {
        return upload;
    }

    public Download getDownload() {
        return download;
    }

    public State getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public SimpleStringProperty timestampProperty() {
        return timestamp;
    }

    private void init() {
        this.id = UUID.randomUUID();
        this.status = State.Current;
        String time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        this.timestamp = new SimpleStringProperty(time);
    }

    public void abort() {
        this.status = State.Fail;
        this.aborted = true;
        this.updateMessage(State.Fail.toString());
    }

    @Override
    protected Void call() throws Exception {
        this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
        Thread.sleep(100);
        this.updateMessage(State.Current.toString());

        switch (type) {
            case Upload:
                while (upload.getProgress().getPercentTransferred() < 100.0 && !upload.isDone()) {
                    updateProgress(upload.getProgress().getBytesTransferred(), upload.getProgress().getTotalBytesToTransfer());
                }
                break;
            case Download:
                while (download.getProgress().getPercentTransferred() < 100.0 && !download.isDone()) {
                    updateProgress(download.getProgress().getBytesTransferred(), download.getProgress().getTotalBytesToTransfer());
                }
        }
        this.updateProgress(1, 1);
        if (aborted) {
            this.updateMessage(State.Fail.toString());
            this.status = State.Fail;
        } else {
            this.updateMessage(State.Success.toString());
            this.status = State.Success;
        }

        return null;
    }

    public boolean isDone() {
        switch (type) {
            case Upload: return upload.isDone();
            case Download: return download.isDone();
            default: return false;
        }
    }

    public String getFile() {
        switch (type) {
            case Upload: return upload.getDescription();
            case Download: return download.getDescription();
            default: return null;
        }
    }
}
