package com.protein.screensnip;

import com.jcraft.jsch.*;
import com.protein.screensnip.tray.TrayItem;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScreenSnip {

    /**
     * Username and password you use to sign into your web server/vps/dedicated server with.
     */
    private static final String USERNAME = "USERNAME", PASSWORD = "PASSWORD";

    /**
     * Port you use to login into your FTP/SFTP server with
     */
    private static final int PORT = 22, FILE_LENGTH = 8;

    /**
     * The IP or domain to the web server/vps/dedicated server.
     */
    private static final String ADDRESS = "IP";

    public static void main(String[] args) {
        new ScreenSnip().start();
    }

    /**
     * @param image the screen snip.
     */
    private void sendImage(BufferedImage image) throws JSchException, IOException, SftpException {
        Session session = new JSch().getSession(USERNAME, ADDRESS, PORT);
        session.setPassword(PASSWORD);
        session.setConfig(new Hashtable() {{
            put("StrictHostKeyChecking", "no");
        }});
        session.connect();
        ChannelSftp channel = (ChannelSftp) session.openChannel(PORT == 22 ? "sftp" : "ftp");
        channel.connect();
        File file = new File(System.getProperty("user.home") + "/Desktop/ScreenShot-Backups", randomString() + ".png");
        ImageIO.write(image, "png", file);
        channel.put(new FileInputStream(file), "/var/www/html/" + file.getName(), ChannelSftp.OVERWRITE);
        Desktop.getDesktop().browse(URI.create("http://" + ADDRESS + "/" + file.getName()));
        channel.disconnect();
        session.disconnect();
    }

    private void start() {
        new TrayItem();
        GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
        Runtime.getRuntime().addShutdownHook(new Thread(keyboardHook::shutdownHook));
        keyboardHook.addKeyListener(new GlobalKeyListener() {
            @Override
            public void keyPressed(GlobalKeyEvent event) {
                if ( event.getVirtualKeyCode() == 119 ) {
                    try {
                        sendImage(new Robot(MouseInfo.getPointerInfo().getDevice()).createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())));
                    } catch (AWTException | IOException | JSchException | SftpException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(GlobalKeyEvent event) {
            }
        });
    }

    /**
     * @return random generated string.
     */
    private String randomString() {
        return IntStream.range(0, FILE_LENGTH).mapToObj(i -> String.valueOf(Character.valueOf((char) ThreadLocalRandom.current().nextInt(97, 122)))).collect(Collectors.joining());
    }
}
