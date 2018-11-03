package de.tum.in.tumcampusapp.ui.ticket.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.api.app.exception.NoPrivateKey;
import de.tum.in.tumcampusapp.ui.generic.activity.BaseActivity;
import de.tum.in.tumcampusapp.ui.ticket.EventsController;
import de.tum.in.tumcampusapp.model.ticket.RawEvent;
import de.tum.in.tumcampusapp.model.ticket.RawTicket;
import de.tum.in.tumcampusapp.model.ticket.TicketType;
import de.tum.in.tumcampusapp.ui.ticket.viewmodel.EventViewEntity;
import de.tum.in.tumcampusapp.ui.ticket.viewmodel.TicketViewEntity;
import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.core.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowTicketActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView locationTextView;
    private ImageView ticketQrCodeImageView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView priceTextView;
    private TextView redemptionStateTextView;

    private EventsController eventsController;

    private TicketViewEntity ticket;
    private EventViewEntity event;
    private TicketType ticketType;

    public ShowTicketActivity() {
        super(R.layout.activity_show_ticket);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        initViews();
        loadTicketData();
        setViewData();

        createQRCode(ticket.getCode());
        setWindowBrightnessToFull();
    }

    private void initViews() {
        titleTextView = findViewById(R.id.ticket_event_title);
        locationTextView = findViewById(R.id.ticket_event_location);
        dateTextView = findViewById(R.id.ticket_event_date_time);
        priceTextView = findViewById(R.id.ticket_event_price);
        redemptionStateTextView = findViewById(R.id.ticket_event_redemption_state);
        ticketQrCodeImageView = findViewById(R.id.ticket_qr_code);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.color_primary,
                R.color.tum_A100,
                R.color.tum_A200
        );
        swipeRefreshLayout.setOnRefreshListener(this::loadRedemptionStatus);
    }

    private void loadRedemptionStatus() {
        try {
            TUMCabeClient.getInstance(this)
                    .fetchTicket(this, ticket.getId())
                    .enqueue(new Callback<RawTicket>() {
                        @Override
                        public void onResponse(@NonNull Call<RawTicket> call,
                                               @NonNull Response<RawTicket> response) {
                            RawTicket ticket = response.body();
                            if (response.isSuccessful() && ticket != null) {
                                handleTicketRefreshSuccess(ticket);
                            } else {
                                handleTicketRefreshFailure();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<RawTicket> call, @NonNull Throwable t) {
                            Utils.log(t);
                            handleTicketRefreshFailure();
                        }
                    });
        } catch (NoPrivateKey e) {
            Utils.log(e);
        }
    }

    private void handleTicketRefreshSuccess(RawTicket ticket) {
        this.ticket = TicketViewEntity.create(this, ticket);
        eventsController.insert(ticket);

        setViewData();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void handleTicketRefreshFailure() {
        Utils.showToast(this, R.string.error_something_wrong);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadTicketData() {
        eventsController = new EventsController(this);
        int eventId = getIntent().getIntExtra(Const.KEY_EVENT_ID, 0);

        RawTicket rawTicket = eventsController.getTicketByEventId(eventId);
        ticket = TicketViewEntity.create(this, rawTicket);
        ticketType = eventsController.getTicketTypeById(ticket.getTicketTypeId());

        RawEvent rawEvent = eventsController.getEventById(ticket.getEventId());
        event = EventViewEntity.create(this, rawEvent);
    }

    private void setViewData() {
        titleTextView.setText(event.getTitle());
        dateTextView.setText(event.getFormattedStartTime());
        priceTextView.setText(ticketType.getFormattedPrice());

        String formattedDateTime = ticket.getFormattedRedemptionDate();
        String redemptionState = getString(R.string.redeemed_format_string, formattedDateTime);
        redemptionStateTextView.setText(redemptionState);

        locationTextView.setText(event.getLocality());
        locationTextView.setOnClickListener(this::showMap);
    }

    private void showMap(View view) {
        TextView textView = (TextView) view;
        String url = "http://maps.google.co.in/maps?q=" + textView.getText();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void setWindowBrightnessToFull() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        window.setAttributes(lp);
    }

    private void createQRCode(String text) {
        Writer multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(
                    text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ticketQrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Utils.log(e);
            Utils.showToast(this, R.string.error_something_wrong);
            finish();
        }
    }

}


