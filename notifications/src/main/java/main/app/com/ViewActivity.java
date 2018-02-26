package main.app.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import main.app.com.keep.NotificationModel;
import main.app.com.notifications.R;


public class ViewActivity extends AppCompatActivity {

    public static final String ARG_NOTIFICATION = "ViewActivity_arg_1";

    private NotificationModel notification;

    private TextView tvTitle;
    private TextView tvBody;
    private Button btnView;

    public static Intent createIntent(Context context, NotificationModel notification) {
        Intent intent = new Intent(context, ViewActivity.class);
        intent.putExtra(ARG_NOTIFICATION, notification);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        notification = (NotificationModel) getIntent().getSerializableExtra(ARG_NOTIFICATION);
        tvTitle = findViewById(R.id.view_tvTitle);
        tvBody = findViewById(R.id.view_tvBody);
        btnView = findViewById(R.id.view_btnView);
        initViews();
    }

    private void initViews() {
        tvTitle.setText(notification.getTitle());
        tvBody.setText(ViewUtils.getTextToView(notification.getBody()));
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = ViewUtils.getLinkFromText(notification.getBody());
                if (link != null) {
                    ViewUtils.openLink(ViewActivity.this, link);
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (notification != null && notification.isForce()) {
            return;
        }
        super.onBackPressed();
    }
}
