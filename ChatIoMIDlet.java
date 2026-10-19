import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import java.io.*;

public class ChatIoMIDlet extends MIDlet implements CommandListener, Runnable {
    private Display display;
    private Form form;
    private TextField ipField, msgField;
    private StringItem chatLog;
    private Command connectCmd, sendCmd, exitCmd;
    private SocketConnection socket;
    private InputStream in;
    private OutputStream out;
    private boolean isRunning = true;

    public ChatIoMIDlet() {
        display = Display.getDisplay(this);
        form = new Form("Chat.Io");
        
        ipField = new TextField("Server IP:", "192.168.43.1", 15, TextField.ANY);
        msgField = new TextField("Mesaj:", "", 50, TextField.ANY);
        chatLog = new StringItem("Söhbət:\n", "");
        
        connectCmd = new Command("Qoşul", Command.OK, 1);
        sendCmd = new Command("Göndər", Command.OK, 2);
        exitCmd = new Command("Çıxış", Command.EXIT, 3);
        
        form.append(ipField);
        form.append(msgField);
        form.append(chatLog);
        
        form.addCommand(connectCmd);
        form.addCommand(sendCmd);
        form.addCommand(exitCmd);
        form.setCommandListener(this);
    }

    public void startApp() { display.setCurrent(form); }
    public void pauseApp() {}
    public void destroyApp(boolean u) { isRunning = false; }

    public void commandAction(Command c, Displayable d) {
        if (c == connectCmd) {
            new Thread(this).start();
        } else if (c == sendCmd) {
            try {
                if (out != null) {
                    String text = msgField.getString();
                    out.write((text + "\n").getBytes());
                    out.flush();
                    chatLog.setText(chatLog.getText() + "\nMən: " + text);
                    msgField.setString("");
                }
            } catch (Exception e) {}
        } else if (c == exitCmd) {
            destroyApp(true);
            notifyDestroyed();
        }
    }

    public void run() {
        try {
            chatLog.setText("Chat.Io qoşulur...");
            socket = (SocketConnection) Connector.open("socket://" + ipField.getString() + ":12345");
            in = socket.openInputStream();
            out = socket.openOutputStream();
            chatLog.setText("Chat.Io Qoşuldu!\n");
            
            byte[] buf = new byte[256];
            while (isRunning) {
                int len = in.read(buf);
                if (len > 0) {
                    chatLog.setText(chatLog.getText() + "\nDostum: " + new String(buf, 0, len));
                }
            }
        } catch (Exception e) {
            chatLog.setText("Xəta: Bağlantı qurulmadı");
        }
    }
}
