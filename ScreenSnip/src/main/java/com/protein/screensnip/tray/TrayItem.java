package com.protein.screensnip.tray;

import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;

import javax.imageio.ImageIO;
import java.io.IOException;

public class TrayItem {

    private SystemTray systemTray;

    public TrayItem() {
        this.systemTray = SystemTray.get();
        if ( systemTray == null ) {
            throw new RuntimeException("Unable to load SystemTray!");
        }
        systemTray.setTooltip("ScreenSnip");

        try {
            systemTray.setImage(ImageIO.read(getClass().getResource("/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        systemTray.getMenu().add(new MenuItem("Quit", e -> {
            systemTray.shutdown();
            System.exit(0);
        }));
    }
}
