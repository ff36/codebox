package co.ff36.controller;

import co.ff36.util.SettingUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Class dedicated to handling all functions relating to the web view modal of the application.
 *
 * Created by tarka on 31/05/2016.
 */
public class WebViewController implements Initializable {

    @FXML
    private WebView webView;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Map<String, String> settings = new SettingUtil().load();
        String address = settings.get(SettingUtil.BROWSER_KEY);
        address = address == null || address.equals("") ? "http://google.com" : address;

        WebEngine webEngine = webView.getEngine();
        webEngine.load(address);
    }
}
