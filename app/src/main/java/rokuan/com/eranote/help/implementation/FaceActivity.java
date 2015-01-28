package rokuan.com.eranote.help.implementation;

import android.support.v7.app.ActionBarActivity;

import rokuan.com.eranote.help.IFaceActivity;
import rokuan.com.eranote.help.IFaceHelpBar;

/**
 * Created by Christophe on 22/01/2015.
 */
public abstract class FaceActivity extends ActionBarActivity implements IFaceActivity {
    private IFaceHelpBar helpBar;

    @Override
    public void setHelpBar(IFaceHelpBar bar) {
        this.helpBar = bar;
    }

    @Override
    public IFaceHelpBar getHelpBar() {
        return helpBar;
    }
}
