package rokuan.com.eranote.help;

/**
 * Created by Christophe on 22/01/2015.
 */
public interface IFaceHelpBar {
    void activate();
    void setMessage(String message);
    void deactivate();
    boolean isActivated();
}
