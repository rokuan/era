package rokuan.com.eranote.help.implementation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import rokuan.com.eranote.R;
import rokuan.com.eranote.help.IFaceHelpBar;

/**
 * Created by Christophe on 22/01/2015.
 */
public class EraHelpBar extends LinearLayout implements IFaceHelpBar, CompoundButton.OnCheckedChangeListener {
    private ToggleButton state;
    private TextView messageBox;

    public EraHelpBar(Context context) {
        super(context);
    }

    public EraHelpBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public EraHelpBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews(){
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.era_help_bar, this);

        messageBox = (TextView)this.findViewById(R.id.help_bar_text);
        state = (ToggleButton)this.findViewById(R.id.help_bar_state);

        state.setOnCheckedChangeListener(this);
    }

    @Override
    public void activate() {
        // TODO: afficher la barre et le message d'activation etc
        //state.setChecked(true);
    }

    @Override
    public void setMessage(String message) {
        messageBox.setText(message);
    }

    @Override
    public void deactivate() {
        // TODO: cacher les messages etc
    }

    @Override
    public boolean isActivated() {
        return state.isChecked();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            this.activate();
        } else {
            this.deactivate();
        }
    }
}
