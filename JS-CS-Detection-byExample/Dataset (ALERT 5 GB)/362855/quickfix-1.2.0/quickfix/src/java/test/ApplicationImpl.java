import org.quickfix.Application;
import org.quickfix.SessionID;
import org.quickfix.Message;
import org.apache.log4j.Category;

public class ApplicationImpl implements Application {
    private static Category category = Category.getInstance
        (ApplicationImpl.class.getName());
    private boolean loggedOn = false;
    private boolean created = false;
    private boolean stopRunning = false;
    private Message message = null;

    public void onCreate(SessionID sessionId) {
        created = true;
        category.debug("onCreate");
    }

    public void onLogon(SessionID sessionId) {
        loggedOn = true;
        category.debug("onLogon");
    }

    public void onLogout(SessionID sessionId) {
        loggedOn = false;
        category.debug("onLogout");
    }

    public void toAdmin(Message message, SessionID sessionId) {
        category.debug(message);
    }

    public void toApp(Message message, SessionID sessionId) {
        category.debug(message);
    }

    public void fromAdmin(Message message, SessionID sessionId) {
        category.debug(message);
    }

    public void fromApp(Message message, SessionID sessionId) {
        category.debug(message);
        this.message = message;
    }

    public void onRun() {
        while(true) {
            try {
                if(stopRunning) return;
                Thread.sleep(1000);
            }
            catch(Exception e) {
            }
        }
    }

    public void stop() { stopRunning = true; }

    public boolean isLoggedOn() {
        return loggedOn;
    }

    public boolean isCreated() {
        return created;
    }

    public Message getMessage() {
        try {
            for(int i = 0; i < 50; ++i) {
                if(message != null) {
                    return message;
                }
            Thread.sleep(100);
            }
        } catch(Exception e) {
        }

        return message;
    }
}
