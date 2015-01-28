package rokuan.com.eranote.help;

/**
 * Created by Christophe on 22/01/2015.
 */
public interface IFaceHelpBar {
    public void activate();
    public void setMessage(String message);
    public void deactivate();
    public boolean isActivated();
}
