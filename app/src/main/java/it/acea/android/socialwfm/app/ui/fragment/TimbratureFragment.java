package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.widget.SlidingTabLayout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.MutableDateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.app.model.ess.TimbraturaMessaggio;
import it.acea.android.socialwfm.app.model.ess.TimbraturaMotivo;
import it.acea.android.socialwfm.app.model.ess.TimbraturaTipo;
import it.acea.android.socialwfm.app.ui.MainActivity;
import it.acea.android.socialwfm.app.ui.adapter.TimbraturePagerAdapter;
import it.acea.android.socialwfm.app.ui.dialog.ErrorDialog;
import it.acea.android.socialwfm.app.ui.dialog.LegendaDialog;
import it.acea.android.socialwfm.app.ui.dialog.TimbraturaAddEditDialog;
import it.acea.android.socialwfm.app.ui.dialog.TimbratureMessaggiDialog;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentTimbratureCalendario;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentTimbratureLista;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;
import tr.xip.errorview.ErrorView;

/**
 * Created by n.fiorillo on 11/03/2016.
 */
public class TimbratureFragment extends EssFragment implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String TAG = TimbratureFragment.class.getSimpleName();
    @Bind(R.id.error_view)
    ErrorView errorView;

    @Bind(R.id.timbrature_view_pager)
    ViewPager mPager;

    @Bind(R.id.tab_layout)
    SlidingTabLayout slidingTabLayout;

    private Calendar dataRequest = Calendar.getInstance();
    private TimbraturePagerAdapter pagerAdapter;
    private List<TimbraturaTipo> tipiTimbratura;
    private List<TimbraturaMotivo> motiviTimbratura;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timbrature, container, false);
        ButterKnife.bind(this, view);
        sendDate();
        initViewPager();
        initTabstrip();
        initErrorView();
        initDate();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchData();
        mPager.setCurrentItem(1);
    }

    @Override
    public ErrorView getErrorView() {
        return errorView;
    }


    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.menu_timbrature);
        item.setIcon(R.drawable.ic_timbrature_on);
        menu.findItem(R.id.menu_legenda).setVisible(true);
        menu.findItem(R.id.menu_messaggi).setVisible(true);
        boolean isLista = pagerAdapter.getItem(mPager.getCurrentItem()) instanceof FragmentTimbratureLista;
        menu.findItem(R.id.menu_ordina).setVisible(isLista);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String tag = Navigator.with(getActivity()).utils().getActualTag();
        Log.d(TAG, tag);
        if (!tag.endsWith(TimbratureFragment.class.getName())) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case R.id.menu_legenda:
                    new LegendaDialog.LegendaTimbratureDialog(getContext()).show();
                    return true;
                case R.id.menu_ordina:
                    EventBus.getDefault().post(new MessageOrdinaLista());
                    return true;
                case R.id.menu_aggiorna:
                    fetchData();
                    return true;
                case R.id.menu_messaggi:
                    showTimbratureMessaggi();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void showIntro() {
        int currentPage = this.mPager.getCurrentItem();
        switch (currentPage){
            case 0:{
                //Giustificativi lista
                ((FragmentTimbratureLista)pagerAdapter.getItem(currentPage)).showIntro();
                break;
            }
            case 1:{
                // Giustificativi calendario
                ((FragmentTimbratureCalendario)pagerAdapter.getItem(currentPage)).showIntro();
                break;
            }
        }
    }

    private void fetchData() {
        fetchTimbrature();
        fetchTimbraturaTipo();
        fetchTimbraturaMotivo();
    }


    private Timbratura getFakeTimbratura() {

        /*if(dataRequest.getTime().equals(new GregorianCalendar(2018,5,11).getTime()) ||
                dataRequest.getTime().equals(new GregorianCalendar(2018,5,12).getTime()) ||
                dataRequest.getTime().equals(new GregorianCalendar(2018,5,13).getTime()) ||
                dataRequest.getTime().equals(new GregorianCalendar(2018,5,14).getTime()) ||
                dataRequest.getTime().equals(new GregorianCalendar(2018,5,15).getTime()) ||
                dataRequest.getTime().equals(new GregorianCalendar(2018,5,16).getTime()) ||
                dataRequest.getTime().equals(new GregorianCalendar(2018,5,17).getTime())) {*/

        //if(true) {
            /*Timbratura.TimbraturaDettaglio td = new Timbratura.TimbraturaDettaglio();
            td.setApprovatore("Giovanni Mucciaccia");
            td.setApprovatoreReadOnly(true);
            td.setCentroDiCostoVisible(false);
            td.setGiorno(new GregorianCalendar(2018,5,15).getTime());
            td.setIdRichiesta("ABC");
            td.setIdSocieta("DEF");
            td.setMotivo("Ritardo Acea800");
            td.setMotivoReadOnly(true);
            td.setOra(new GregorianCalendar(2018,5,15).getTime());
            td.setTipo("Uscita");
            td.setStato("Ok");
            td.setMotivoVisible(true);*/

            Timbratura.TimbraturaOrario to = new Timbratura.TimbraturaOrario();
            to.setDescrizione("Registrazione ingresso/uscita");
            to.setIdRichesta("DEF");
            to.setInfo("DEF");
            to.setOraDescrizione("15:50");
            //to.setDettaglio(td);
            //to.setGiorno(tg);
            to.setOrario(new GregorianCalendar(2018,5,15,15,50).getTime());
            to.setTipo(Utils.ESS_COLORS.LAV.name());
            to.setTipoDescrizione("Al lavoro");

            Timbratura.TimbraturaOrario to1 = new Timbratura.TimbraturaOrario();
            to1.setDescrizione("Registrazione ingresso/uscita");
            to1.setIdRichesta("ABC");
            to1.setInfo("ABC");
            to1.setOraDescrizione("08:50");
            //to.setDettaglio(td);
            //to.setGiorno(tg);
            to1.setOrario(new GregorianCalendar(2018,5,15,8,50).getTime());
            to1.setTipo(Utils.ESS_COLORS.LAV.name());
            to1.setTipoDescrizione("Al lavoro");

            Timbratura.TimbraturaGiorno tg1 = new Timbratura.TimbraturaGiorno();
            tg1.setGiorno(new GregorianCalendar(2018,5,11).getTime());
            tg1.setPianificato("08:15 - 16:36");
            tg1.setPianificatoInfo("");

            Timbratura.TimbraturaGiorno tg2 = new Timbratura.TimbraturaGiorno();
            tg2.setGiorno(new GregorianCalendar(2018,5,12).getTime());
            tg2.setPianificato("10:15 - 14:36");
            tg2.setPianificatoInfo("");

            Timbratura.TimbraturaGiorno tg3 = new Timbratura.TimbraturaGiorno();
            tg3.setGiorno(new GregorianCalendar(2018,5,13).getTime());
            tg3.setPianificato("08:00 - 18:00");
            tg3.setPianificatoInfo("");

            Timbratura.TimbraturaGiorno tg4 = new Timbratura.TimbraturaGiorno();
            tg4.setGiorno(new GregorianCalendar(2018,5,14).getTime());
            tg4.setPianificato("08:15 - 19:36");
            tg4.setPianificatoInfo("");

            Timbratura.TimbraturaGiorno tg5 = new Timbratura.TimbraturaGiorno();
            tg5.setGiorno(new GregorianCalendar(2018,5,15).getTime());
            tg5.setPianificato("08:15 - 12:36");
            tg5.setPianificatoInfo("");

            Timbratura.TimbraturaGiorno tg6 = new Timbratura.TimbraturaGiorno();
            tg6.setGiorno(new GregorianCalendar(2018,5,16).getTime());
            tg6.setPianificato("12:15 - 16:36");
            tg6.setPianificatoInfo("");

            Timbratura.TimbraturaGiorno tg7 = new Timbratura.TimbraturaGiorno();
            tg7.setGiorno(new GregorianCalendar(2018,5,17).getTime());
            tg7.setPianificato("08:15 - 13:16");
            tg7.setPianificatoInfo("");


            ArrayList<Timbratura.TimbraturaOrario> tt = new ArrayList<Timbratura.TimbraturaOrario>();
            tt.add(to1);
            tt.add(to);
            tg5.setOrari(tt);

            ArrayList<Timbratura.TimbraturaGiorno> tgt = new ArrayList<>();
            tgt.add(tg1);
            tgt.add(tg2);
            tgt.add(tg3);
            tgt.add(tg4);
            tgt.add(tg5);
            tgt.add(tg6);
            tgt.add(tg7);

            Timbratura t = new Timbratura(tgt);

            return t;


        //} else
        //    return null;

    }

    private void fetchTimbrature() {
        showProgressDialog();
        EssClient.fetchTimbratura(getContext(), dataRequest.getTime(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                Timbratura timbratura = null;
                try {
                    timbratura = getFakeTimbratura(); //ResponseManager.getTimbratura(e, result);
                } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                    new ErrorDialog(getContext(), dataErrorExpection.getErrorDetails()).show();
                    dataErrorExpection.printStackTrace();
                } */catch (Exception exception) {
                    exception.printStackTrace();
                    showError();
                } finally {
                    EventBus.getDefault().postSticky(new MessageTimbraburaEvent(timbratura));
                    dismissDialog();
                }
            }
        });
    }


    private ArrayList<TimbraturaTipo> getFakeTimbTipo() {

        TimbraturaTipo t = new TimbraturaTipo();
        t.setDescrizione("Registrazione ingresso/uscita");
        t.setTipo("Uscita");

        TimbraturaTipo t1 = new TimbraturaTipo();
        t1.setDescrizione("Registrazione ingresso/uscita");
        t1.setTipo("Entrata");

        ArrayList<TimbraturaTipo> tt = new ArrayList<>();
        tt.add(t1);
        tt.add(t);
        return tt;

    }

    private void fetchTimbraturaTipo() {
        showProgressDialog();
        EssClient.fetchTimbraturaTipo(getContext(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    tipiTimbratura = getFakeTimbTipo(); //ResponseManager.getListaTimbraturaTipo(e, result);
                    //EventBus.getDefault().postSticky(new MessageListaTipiTimbratura(tipiTimbratura));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    showError();
                } finally {
                    dismissDialog();
                }
            }
        });
    }


    private ArrayList<TimbraturaMotivo> getFakeMotivi() {

        TimbraturaMotivo t1 = new TimbraturaMotivo();
        t1.setId("ABC");
        t1.setDescrizione("Registrazione ingresso/uscita");

        TimbraturaMotivo t2 = new TimbraturaMotivo();
        t2.setId("DEF");
        t2.setDescrizione("Registrazione ingresso/uscita");

        ArrayList<TimbraturaMotivo> t = new ArrayList<>();
        t.add(t1);
        t.add(t2);
        return t;

    }

    private void fetchTimbraturaMotivo() {
        showProgressDialog();
        EssClient.fetchTimbraturaMotivo(getContext(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    motiviTimbratura = getFakeMotivi(); //ResponseManager.getListaTimbraturaMotivo(e, result);
                } catch (Exception e1) {
                    showError();
                    e1.printStackTrace();
                } finally {
                    dismissDialog();
                }
            }
        });
    }

    private void initDate() {
        dataRequest.set(Calendar.HOUR_OF_DAY, 0);
        dataRequest.set(Calendar.MINUTE, 0);
        dataRequest.set(Calendar.SECOND, 0);
    }

    private void initTabstrip() {
        slidingTabLayout.setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.secondary_ess));
        slidingTabLayout.setTextColor(R.color.md_blue_grey_800);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(this.mPager);
    }

    private void initViewPager() {
        pagerAdapter = new TimbraturePagerAdapter(getContext(), getChildFragmentManager());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPager.setAdapter(pagerAdapter);
    }

    private void initErrorView() {
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                fetchData();
                dismissError();
            }
        });
    }


    private ArrayList<TimbraturaMessaggio> getFakeMsg() {

        TimbraturaMessaggio t1 = new TimbraturaMessaggio();
        t1.setData(new GregorianCalendar(2018,5,11).getTime());
        t1.setMessaggio("abc");

        TimbraturaMessaggio t2 = new TimbraturaMessaggio();
        t2.setData(new GregorianCalendar(2018,5,12).getTime());
        t2.setMessaggio("def");

        TimbraturaMessaggio t3 = new TimbraturaMessaggio();
        t3.setData(new GregorianCalendar(2018,5,13).getTime());
        t3.setMessaggio("ghi");

        TimbraturaMessaggio t4 = new TimbraturaMessaggio();
        t4.setData(new GregorianCalendar(2018,5,14).getTime());
        t4.setMessaggio("jkl");

        TimbraturaMessaggio t5 = new TimbraturaMessaggio();
        t5.setData(new GregorianCalendar(2018,5,15).getTime());
        t5.setMessaggio("mno");

        TimbraturaMessaggio t6 = new TimbraturaMessaggio();
        t6.setData(new GregorianCalendar(2018,5,16).getTime());
        t6.setMessaggio("pqr");

        TimbraturaMessaggio t7 = new TimbraturaMessaggio();
        t7.setData(new GregorianCalendar(2018,5,17).getTime());
        t7.setMessaggio("stu");

        ArrayList<TimbraturaMessaggio> t = new ArrayList<>();
        t.add(t1);
        t.add(t2);
        t.add(t3);
        t.add(t4);
        t.add(t5);
        t.add(t6);
        t.add(t7);

        return t;

    }



    public void showTimbratureMessaggi() {
        showProgressDialog();
        EssClient.fetchTimbratureMessaggioList(getContext(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    List<TimbraturaMessaggio> list = getFakeMsg(); //ResponseManager.getListaTimbraturaMessaggio(e, result);
                    if (list != null) Collections.sort(list, TimbraturaMessaggio.DATA_DESC);
                    new TimbratureMessaggiDialog(getContext(), list).show();

                } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                    new ErrorDialog(getContext(), dataErrorExpection.getErrorDetails()).show();
                } */catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    dismissDialog();
                }
            }
        });
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        dateSetAndFetchData(year, monthOfYear, dayOfMonth);
    }

    private void dateSetAndFetchData(int year, int monthOfYear, int dayOfMonth) {
        dataRequest.clear();
        dataRequest.set(year, monthOfYear, dayOfMonth);
        sendDate();
        fetchData();
    }

    public void sendDate() {
        EventBus.getDefault().postSticky(new MessageDateSet(dataRequest.get(Calendar.YEAR),
                dataRequest.get(Calendar.MONTH), dataRequest.get(Calendar.DAY_OF_MONTH)));
    }

    @Subscribe
    public void showDatePicker(MessageShowDatePicker event) {
        int y = dataRequest.get(Calendar.YEAR);
        int m = dataRequest.get(Calendar.MONTH);
        int d = dataRequest.get(Calendar.DAY_OF_MONTH);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
        cdp.setPreselectedDate(y, m, d);
        cdp.setOnDateSetListener(this);
        cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        cdp.show(getFragmentManager(), "");
    }

    @Subscribe
    public void showModificaTimbratura(MessageModificaTimbratura event) {
        new TimbraturaAddEditDialog(getContext(), getFragmentManager(), tipiTimbratura, motiviTimbratura,
                event.timbraturaDettaglio, TimbraturaAddEditDialog.DialogMode.MODIFICA).show();
    }

    @Subscribe
    public void showCancellaTimbratura(MessageCancellaTimbratura event) {
        new TimbraturaAddEditDialog(getContext(), getFragmentManager(), tipiTimbratura, motiviTimbratura,
                event.timbraturaDettaglio, TimbraturaAddEditDialog.DialogMode.CANCELLA).show();
    }

    @Subscribe
    public void showAggiungiTimbratura(MessageAggiungiTimbratura event) {
        new TimbraturaAddEditDialog(getContext(), getFragmentManager(), tipiTimbratura, motiviTimbratura).show();
    }

    @Subscribe
    public void createTimbratura(final MessageCreaTimbratura event) {
        showProgressDialog();
        EssClient.fetchXCSRFToken(getContext(), EssClient.EssEntity.TIMBRATURE_DETAIL, new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    String token = ResponseManager.getXCSRFToken(e, result);
                    showProgressDialog();
                    EssClient.createTimbratura(getContext(), token, event.timbraturaDettaglio, new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                Timbratura.TimbraturaDettaglio t = ResponseManager.createTimbratura(e, result);
                                onTimbraturaResponse(t, event.modificheCompletate);
                            } catch (ResponseManager.DataErrorExpection errorExpection) {
                                new ErrorDialog(getContext(), errorExpection.getErrorDetails()).show();
                                EventBus.getDefault().post(new MessageErroreTimbratura());
                            } catch (Exception e1) {
                                showErroreConnessione();
                                e1.printStackTrace();
                            } finally {
                                dismissDialog();
                            }
                        }
                    });
                } catch (Exception e1) {
                    showErroreConnessione();
                    e1.printStackTrace();
                } finally {
                    dismissDialog();
                }
            }
        });
    }

    @Subscribe
    public void modificaTimbratura(final MessageRequestModificaTimbratura event) {
        showProgressDialog();
        EssClient.fetchXCSRFToken(getContext(), EssClient.EssEntity.TIMBRATURE_DETAIL, new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    String token = ResponseManager.getXCSRFToken(e, result);
                    showProgressDialog();
                    EssClient.modificaTimbratura(getContext(), token, event.timbraturaDettaglio, new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                if (result.getRequest().getMethod().equals("PUT") && result.getHeaders().code() == 204) {
                                    EventBus.getDefault().post(new MessageResponseModificaTimbraturaCompletata());
                                } else {
                                    Timbratura.TimbraturaDettaglio t = ResponseManager.createTimbratura(e, result);
                                    EventBus.getDefault().post(new MessageResponseModificaTimbratura(t, event.modificheCompletate));
                                }
                            } catch (ResponseManager.DataErrorExpection errorExpection) {
                                new ErrorDialog(getContext(), errorExpection.getErrorDetails()).show();
                                EventBus.getDefault().post(new MessageErroreTimbratura());
                            }
                            catch (Exception e1) {
                                showErroreConnessione();
                                e1.printStackTrace();
                            } finally {
                                dismissDialog();
                            }
                        }
                    });
                } catch (Exception e1) {
                    showErroreConnessione();
                    e1.printStackTrace();
                } finally {
                    dismissDialog();
                }
            }
        });
    }

    private void onTimbraturaResponse(Timbratura.TimbraturaDettaglio timbraturaDettaglio, boolean modificheCompletate) {
        if (timbraturaDettaglio.getIdRichiesta() != null && !timbraturaDettaglio.getIdRichiesta().isEmpty()) {
            EventBus.getDefault().post(new MessageResponseCreateTimbraturaCompletato(timbraturaDettaglio));
        } else {
            EventBus.getDefault().post(new MessageResponseCreaTimbratura(timbraturaDettaglio,
                    modificheCompletate));
        }
    }

    @Subscribe
    public void onRequestPreviousWeekData(MessageRequestWeekData event) {
        MutableDateTime d = new MutableDateTime(dataRequest);
        d.addWeeks(event.weeks);
        dataRequest = d.toGregorianCalendar();
        fetchData();
        sendDate();
    }

    @Subscribe
    public void onBackStackChanged(MainActivity.BackStackChanged event) {
        setSubtitle(R.string.timbrature);
    }


    public static class MessageTimbraburaEvent {
        public Timbratura timbratura;

        public MessageTimbraburaEvent(Timbratura timbratura) {
            this.timbratura = timbratura;
        }
    }

    private void showErroreConnessione() {
        //Toast.makeText(getContext(), "Si è verificato un errore", Toast.LENGTH_SHORT).show();
        new MaterialDialog.Builder(getContext())
                .title(R.string.attenzione)
                .content(R.string.si_e_verificato_un_errore)
                .positiveText(R.string.chiudi)
                .autoDismiss(true)
                .show();
    }

    @Subscribe
    public void onRequestUpdate(MessageRequestUpdate event) {
        fetchData();
    }

    @Subscribe
    public void onRequestDataByDate(MessageRequestDataByDate event) {
        dateSetAndFetchData(event.year, event.monthOfYear, event.dayOfMonth);
    }

    @Subscribe
    public void onCreateTimbraturaCompletato(MessageResponseCreateTimbraturaCompletato event) {
        EventBus.getDefault().post(new MessageRequestUpdate());
    }

    @Subscribe
    public void onCreateTimbraturaCompletato(MessageResponseModificaTimbraturaCompletata event) {
        EventBus.getDefault().post(new MessageRequestUpdate());
    }

    public static class MessageRequestUpdate {
    }

    static abstract class MessageRequestWeekData {
        int weeks;
    }

    public static class MessageRequestDataByDate {
        public int year;
        public int monthOfYear;
        public int dayOfMonth;

        public MessageRequestDataByDate(int year, int monthOfYear, int dayOfMonth) {
            this.year = year;
            this.monthOfYear = monthOfYear;
            this.dayOfMonth = dayOfMonth;
        }

    }

    public static class MessageRequestPreviousWeekData extends MessageRequestWeekData {
        public MessageRequestPreviousWeekData() {
            this.weeks = -1;
        }
    }

    public static class MessageRequestNextWeekData extends MessageRequestWeekData {
        public MessageRequestNextWeekData() {
            this.weeks = 1;
        }
    }

    public static class MessageOrdinaLista {
    }

    public static class MessageShowDatePicker {
    }

    public static class MessageDateSet {
        public int year;
        public int monthOfYear;
        public int dayOfMonth;

        public MessageDateSet(int year, int monthOfYear, int dayOfMonth) {
            this.year = year;
            this.monthOfYear = monthOfYear;
            this.dayOfMonth = dayOfMonth;
        }
    }

    public static class MessageModificaTimbratura {
        public Timbratura.TimbraturaDettaglio timbraturaDettaglio;

        public MessageModificaTimbratura(Timbratura.TimbraturaDettaglio timbraturaDettaglio) {
            this.timbraturaDettaglio = timbraturaDettaglio;
        }
    }

    public static class MessageCancellaTimbratura {
        public Timbratura.TimbraturaDettaglio timbraturaDettaglio;

        public MessageCancellaTimbratura(Timbratura.TimbraturaDettaglio timbraturaDettaglio) {
            this.timbraturaDettaglio = timbraturaDettaglio;
        }
    }

    public static class MessageAggiungiTimbratura {
    }


    public static class MessageCreaTimbratura {
        public boolean modificheCompletate = false;
        public Timbratura.TimbraturaDettaglio timbraturaDettaglio;

        public MessageCreaTimbratura(Timbratura.TimbraturaDettaglio timbraturaDettaglio, boolean modificheCompletate) {
            this.timbraturaDettaglio = timbraturaDettaglio;
            this.modificheCompletate = modificheCompletate;
        }
    }


    public static class MessageResponseCreaTimbratura {
        public Timbratura.TimbraturaDettaglio timbraturaDettaglio;
        public boolean modificheCompletate = false;

        public MessageResponseCreaTimbratura(Timbratura.TimbraturaDettaglio timbraturaDettaglio, boolean modificheCompletate) {
            this.timbraturaDettaglio = timbraturaDettaglio;
            this.modificheCompletate = modificheCompletate;
        }
    }

    public static class MessageResponseCreateTimbraturaCompletato {
        public Timbratura.TimbraturaDettaglio timbraturaDettaglio;

        public MessageResponseCreateTimbraturaCompletato(Timbratura.TimbraturaDettaglio timbraturaDettaglio) {
            this.timbraturaDettaglio = timbraturaDettaglio;
        }
    }

    public static class MessageErroreTimbratura {}

    public static class MessageRequestModificaTimbratura {
        public Timbratura.TimbraturaDettaglio timbraturaDettaglio;
        public boolean modificheCompletate;

        public MessageRequestModificaTimbratura(Timbratura.TimbraturaDettaglio timbraturaDettaglio, boolean modificheCompletate) {
            this.timbraturaDettaglio = timbraturaDettaglio;
            this.modificheCompletate = modificheCompletate;
        }
    }

    public static class MessageResponseModificaTimbratura {
        public Timbratura.TimbraturaDettaglio timbraturaDettaglio;
        public boolean modificheCompletate;

        public MessageResponseModificaTimbratura(Timbratura.TimbraturaDettaglio timbraturaDettaglio, boolean modificheCompletate) {
            this.timbraturaDettaglio = timbraturaDettaglio;
            this.modificheCompletate = modificheCompletate;
        }
    }

    public static class MessageResponseModificaTimbraturaCompletata {}
}
