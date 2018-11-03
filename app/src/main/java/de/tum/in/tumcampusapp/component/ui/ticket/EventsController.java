package de.tum.in.tumcampusapp.component.ui.ticket;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.api.app.exception.NoPrivateKey;
import de.tum.in.tumcampusapp.api.shared.CacheControl;
import de.tum.in.tumcampusapp.database.daos.EventDao;
import de.tum.in.tumcampusapp.database.daos.TicketDao;
import de.tum.in.tumcampusapp.database.daos.TicketTypeDao;
import de.tum.in.tumcampusapp.model.chat.ChatMember;
import de.tum.in.tumcampusapp.component.ui.overview.card.Card;
import de.tum.in.tumcampusapp.component.ui.overview.card.ProvidesCard;
import de.tum.in.tumcampusapp.model.ticket.RawEvent;
import de.tum.in.tumcampusapp.model.ticket.RawTicket;
import de.tum.in.tumcampusapp.model.ticket.TicketType;
import de.tum.in.tumcampusapp.component.ui.ticket.viewmodel.EventViewEntity;
import de.tum.in.tumcampusapp.database.TcaDb;
import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.core.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class is responsible for providing ticket and event data to the activities.
 * For that purpose it handles both server and database accesses.
 */
public class EventsController implements ProvidesCard {

    private final Context context;

    private final EventDao eventDao;
    private final TicketDao ticketDao;
    private final TicketTypeDao ticketTypeDao;

    /**
     * Constructor, open/create database, create table if necessary
     *
     * @param context Context
     */
    public EventsController(Context context) {
        this.context = context;
        TcaDb db = TcaDb.getInstance(context);
        eventDao = db.eventDao();
        ticketDao = db.ticketDao();
        ticketTypeDao = db.ticketTypeDao();
    }

    public void downloadFromService() {
        Callback<List<RawEvent>> eventCallback = new Callback<List<RawEvent>>() {

            @Override
            public void onResponse(@NonNull Call<List<RawEvent>> call, Response<List<RawEvent>> response) {
                List<RawEvent> events = response.body();
                if (events == null) {
                    return;
                }
                storeEvents(events);
            }

            @Override
            public void onFailure(@NonNull Call<List<RawEvent>> call, @NonNull  Throwable t) {
                Utils.log(t);
            }
        };

        Callback<List<RawTicket>> ticketCallback = new Callback<List<RawTicket>>() {
            @Override
            public void onResponse(@NonNull Call<List<RawTicket>> call, Response<List<RawTicket>> response) {
                List<RawTicket> tickets = response.body();
                if (tickets == null) {
                    return;
                }
                insert(tickets.toArray(new RawTicket[0]));
                loadTicketTypesForTickets(tickets);
            }

            @Override
            public void onFailure(@NonNull Call<List<RawTicket>> call, @NonNull Throwable t) {
                Utils.log(t);
            }
        };

        getEventsAndTicketsFromServer(eventCallback, ticketCallback);
    }

    public void getEventsAndTicketsFromServer(Callback<List<RawEvent>> eventCallback,
                                              Callback<List<RawTicket>> ticketCallback) {
        // Delete all too old items
        eventDao.removePastEvents();

        // Load all events
        TUMCabeClient.getInstance(context).fetchEvents(eventCallback);

        // Load all tickets
        try {
            if (Utils.getSetting(context, Const.CHAT_MEMBER, ChatMember.class) != null) {
                TUMCabeClient.getInstance(context).fetchTickets(context, ticketCallback);
            }
        } catch (NoPrivateKey e) {
            Utils.log(e);
        }
    }

    private void loadTicketTypesForTickets(Iterable<RawTicket> tickets){
        // get ticket type information for all tickets
        for (RawTicket ticket : tickets){
            TUMCabeClient.getInstance(context).fetchTicketTypes(ticket.getEventId(),
                    new Callback<List<TicketType>>(){

                        @Override
                        public void onResponse(@NonNull Call<List<TicketType>> call, @NonNull Response<List<TicketType>> response) {
                            List<TicketType> ticketTypes = response.body();
                            if (ticketTypes == null) {
                                return;
                            }
                            // add found ticket types to database (needed in ShowTicketActivity)
                            addTicketTypes(ticketTypes);
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<TicketType>> call, @NonNull Throwable t) {
                            // if ticketTypes could not be retrieved from server, e.g. due to network problems
                            Utils.log(t);
                        }
                    });

        }
    }

    // Event methods

    public void storeEvents(List<RawEvent> events) {
        eventDao.insert(events);
    }

    public void setDismissed(int id) {
        eventDao.setDismissed(id);
    }

    public LiveData<List<RawEvent>> getEvents() {
        return eventDao.getAll();
    }

    /**
     * @return all events for which a ticket exists
     */
    public MediatorLiveData<List<RawEvent>> getBookedEvents() {
        LiveData<List<RawTicket>> tickets = ticketDao.getAll();
        MediatorLiveData<List<RawEvent>> events = new MediatorLiveData<>();

        events.addSource(tickets, newTickets -> {
            List<RawEvent> bookedEvents = new ArrayList<>();

            for (RawTicket ticket : newTickets) {
                RawEvent event = getEventById(ticket.getEventId());
                // the event may be null if the corresponding event of a ticket has already been deleted
                // these event should not be returned
                if (event != null) {
                    bookedEvents.add(event);
                }
            }

            events.setValue(bookedEvents);
        });

        return events;
    }

    public boolean isEventBooked(int eventId) {
        RawTicket ticket = ticketDao.getByEventId(eventId);
        return ticket != null;
    }

    public RawEvent getEventById(int id) {
        return eventDao.getEventById(id);
    }

    // Ticket methods

    public RawTicket getTicketByEventId(int eventId) {
        return ticketDao.getByEventId(eventId);
    }

    public TicketType getTicketTypeById(int id) {
        return ticketTypeDao.getById(id);
    }

    public void insert(RawTicket... tickets) {
        ticketDao.insert(tickets);
    }

    public void addTicketTypes(List<TicketType> ticketTypes) {
        ticketTypeDao.insert(ticketTypes);
    }

    @NotNull
    @Override
    public List<Card> getCards(@NonNull @NotNull CacheControl cacheControl) {
        List<Card> results = new ArrayList<>();

        // Only add the next upcoming event for now
        RawEvent event = eventDao.getNextEvent();
        if (event != null) {
            EventCard eventCard = new EventCard(context);
            EventViewEntity viewEntity = EventViewEntity.create(context, event);
            eventCard.setEvent(viewEntity);
            results.add(eventCard);
        }

        return results;
    }

}

