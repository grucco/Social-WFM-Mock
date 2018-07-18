package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.ess.DatiPersonali;
import it.acea.android.socialwfm.app.ui.fragment.ProfileFragment;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.PersonInfoResponse;
import it.acea.android.socialwfm.http.response.user.info.PhoneNumbers;

/**
 * Created by Raphael on 04/11/2015.
 */
public class FragmentProfileInfo extends BaseFragment {

    private static final String TAG = FragmentProfileInfo.class.getName();
    @Bind(R.id.codice_fiscale)
    TextView codiceFiscale;
    @Bind(R.id.sesso)
    TextView sesso;
    @Bind(R.id.stato_civile)
    TextView statoCivile;
    @Bind(R.id.cittadinanza)
    TextView cittadinanza;
    @Bind(R.id.data_di_nascita)
    TextView dataDiNascita;
    @Bind(R.id.dati_personale_container)
    CardView datiPersonaleContainer;
    @Bind(R.id.indirizzoResidenza)
    TextView indirizzoResidenza;
    @Bind(R.id.cittaResidenza)
    TextView cittaResidenza;
    @Bind(R.id.capResidenza)
    TextView capResidenza;
    @Bind(R.id.numeroDiTelefonoResidenza)
    TextView numeroDiTelefonoResidenza;

    @Bind(R.id.provinciaResidenza)
    TextView provinciaResidenza;
    @Bind(R.id.numeroCivicoResidenza)
    TextView numeroCivicoResidenza;
    @Bind(R.id.paeseResidenza)
    TextView paeseResidenza;
    @Bind(R.id.residenza)
    CardView residenza;
    @Bind(R.id.residenzaIsDomicilio)
    TextView residenzaIsDomicilio;
    @Bind(R.id.luogoDiNascita)
    TextView luogoDiNascita;
    @Bind(R.id.titolo)
    TextView titolo;
    @Bind(R.id.cognome)
    TextView cognome;
    @Bind(R.id.nome)
    TextView nome;
    @Bind(R.id.cognomeAcquisito)
    TextView cognomeAcquisito;
    @Bind(R.id.data_stato_civile)
    TextView dataStatoCivile;
    @Bind(R.id.numeroFigli)
    TextView numeroFigli;
    @Bind(R.id.paeseDiOrigine)
    TextView paeseDiOrigine;
    @Bind(R.id.provincia)
    TextView provincia;
    @Bind(R.id.lingua)
    TextView lingua;
    @Bind(R.id.gruppo)
    TextView gruppo;
    @Bind(R.id.tipoViaResidenza)
    TextView tipoVia;
    @Bind(R.id.palazzinaResidenza)
    TextView palazzinaResidenza;
    @Bind(R.id.scalaResidenza)
    TextView scalaResidenza;
    @Bind(R.id.internoResidenza)
    TextView internoResidenza;
    @Bind(R.id.regioneResidenza)
    TextView regioneResidenza;
    @Bind(R.id.residenzaHeader)
    TextView residenzaHeader;
    @Bind(R.id.domicilioHeader)
    TextView domicilioHeader;
    @Bind(R.id.tipoViaDomicilio)
    TextView tipoViaDomicilio;
    @Bind(R.id.indirizzoDomicilio)
    TextView indirizzoDomicilio;
    @Bind(R.id.numeroCivicoDomicilio)
    TextView numeroCivicoDomicilio;
    @Bind(R.id.palazzinaDomicilio)
    TextView palazzinaDomicilio;
    @Bind(R.id.scalaDomicilio)
    TextView scalaDomicilio;
    @Bind(R.id.internoDomicilio)
    TextView internoDomicilio;
    @Bind(R.id.cittaDomicilio)
    TextView cittaDomicilio;
    @Bind(R.id.capDomicilio)
    TextView capDomicilio;
    @Bind(R.id.provinciaDomicilio)
    TextView provinciaDomicilio;
    @Bind(R.id.regioneDomicilio)
    TextView regioneDomicilio;
    @Bind(R.id.paeseDomicilio)
    TextView paeseDomicilio;
    @Bind(R.id.numeroDiTelefonoDomicilio)
    TextView numeroDiTelefonoDomicilio;
    @Bind(R.id.domicilio)
    CardView domicilio;
    @Bind(R.id.dati_personali_header)
    TextView datiPersonaliHeader;

    private String userId;

    @Bind(R.id.company_profile)
    TextView company_profile;

    @Bind(R.id.telephone_profile)
    TextView telephone_profile;

    @Bind(R.id.mobile_profile)
    TextView mobile_profile;

    @Bind(R.id.email_profile)
    TextView email_profile;

    private String getAddress;
    private String getPrimaryEmailAddress;
    private String getMobilePhoneNumber;
    private String getWorkPhoneNumberType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userId = getArguments().getString("USER_ID");
        View view = inflater.inflate(R.layout.fragment_profile_info, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread() {
            @Override
            public void run() {
                HttpClientRequest.executeRequestGetMemberProfile(getActivity(), userId, new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            return;
                        }
                        PersonInfoResponse response = new Gson()
                                .fromJson(result.getAsJsonObject("d")
                                        .get("results"), PersonInfoResponse.class);
                        if (response == null) {
                            return;
                        }
                        getAddress = "Via Vittore Carpaccio 26, Roma, RM, Italia, 00147"; //response.getAddress();
                        getPrimaryEmailAddress = response.getPrimaryEmailAddress();
                        getMobilePhoneNumber = "3921234567";
                        getWorkPhoneNumberType = "";
                        /*for (PhoneNumbers numbers : response.getPhoneNumbers().getResults()) {
                            if (numbers.getPhoneNumberType().equals("Mobile"))
                                getMobilePhoneNumber = numbers.getPhoneNumber();
                            if (numbers.getPhoneNumberType().equals("Work"))
                                getWorkPhoneNumberType = numbers.getPhoneNumber();
                        }*/
                        updateUi(getAddress, getPrimaryEmailAddress, getMobilePhoneNumber, getWorkPhoneNumberType);
                    }
                });
            }
        }.start();
    }

    @Subscribe(sticky = true)
    public void onDatiPersonali(ProfileFragment.DatiPersonaliPrelevati event) {
        Log.d(TAG, "Evento dati personali");
        datiPersonaleContainer.setVisibility(View.VISIBLE);
        datiPersonaliHeader.setVisibility(View.VISIBLE);
        titolo.setText(event.datiPersonali.getTitolo());
        nome.setText(event.datiPersonali.getNome());
        cognome.setText(event.datiPersonali.getCognome());
        cognomeAcquisito.setText(event.datiPersonali.getCognomeAcquisito());
        dataDiNascita.setText(event.datiPersonali.getDataDiNascitaText());
        luogoDiNascita.setText(event.datiPersonali.getLuogoDiNascita());
        codiceFiscale.setText(event.datiPersonali.getCodiceFiscale());
        sesso.setText(event.datiPersonali.getSesso());
        statoCivile.setText(event.datiPersonali.getStatoCivile());
        numeroFigli.setText(String.valueOf(event.datiPersonali.getNumeroDiFigli()));
        dataStatoCivile.setText(event.datiPersonali.getInizioStatoCivileText());
        cittadinanza.setText(event.datiPersonali.getCittadinanza());
        paeseDiOrigine.setText(event.datiPersonali.getPaeseDiOrigine());
        provincia.setText(event.datiPersonali.getProvincia());
        lingua.setText(event.datiPersonali.getLingua());
        gruppo.setText(event.datiPersonali.getGruppo());
        for (DatiPersonali.Indirizzo i : event.datiPersonali.getIndirizzi()) {
            if (i.getTipologiaIndirizzo().toUpperCase().equals("RESIDENZA")) {
                domicilioHeader.setVisibility(i.isDomicilioIsResidenza() ? View.GONE: View.VISIBLE);
                domicilio.setVisibility(i.isDomicilioIsResidenza() ? View.GONE: View.VISIBLE);
                bindResidenza(i);
            } else {
                bindDomicilio(i);
            }
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void bindResidenza(DatiPersonali.Indirizzo indirizzo) {
        residenza.setVisibility(View.VISIBLE);
        residenzaHeader.setVisibility(View.VISIBLE);
        indirizzoResidenza.setText(indirizzo.getIndirizzo());
        cittaResidenza.setText(indirizzo.getCitta());
        capResidenza.setText(indirizzo.getCap());
        numeroDiTelefonoResidenza.setText(indirizzo.getNumeroDiTelefono());
        String a = String.format("%s (%s)", indirizzo.getProvincia(), indirizzo.getProvinciaSigla());
        provinciaResidenza.setText(a);
        numeroCivicoResidenza.setText(indirizzo.getNumeroCivico());
        paeseResidenza.setText(indirizzo.getPaese());
        tipoVia.setText(indirizzo.getTipologiaVia());
        palazzinaResidenza.setText(indirizzo.getPalazzina());
        scalaResidenza.setText(indirizzo.getScala());
        internoResidenza.setText(indirizzo.getIntero());
        regioneResidenza.setText(indirizzo.getRegione());
        residenzaIsDomicilio.setText(indirizzo.getDomicilioIsResidenzaDescrizione());
    }

    private void bindDomicilio(DatiPersonali.Indirizzo indirizzo) {
        indirizzoDomicilio.setText(indirizzo.getIndirizzo());
        cittaDomicilio.setText(indirizzo.getCitta());
        capDomicilio.setText(indirizzo.getCap());
        numeroDiTelefonoDomicilio.setText(indirizzo.getNumeroDiTelefono());
        String a = String.format("%s (%s)", indirizzo.getProvincia(), indirizzo.getProvinciaSigla());
        provinciaDomicilio.setText(a);
        numeroCivicoDomicilio.setText(indirizzo.getNumeroCivico());
        paeseDomicilio.setText(indirizzo.getPaese());
        tipoVia.setText(indirizzo.getTipologiaVia());
        palazzinaDomicilio.setText(indirizzo.getPalazzina());
        scalaDomicilio.setText(indirizzo.getScala());
        internoDomicilio.setText(indirizzo.getIntero());
        regioneDomicilio.setText(indirizzo.getRegione());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        EventBus.getDefault().unregister(this);
    }

    private void updateUi(String getAddress, String getPrimaryEmailAddress, String getMobilePhoneNumber, String getWorkPhoneNumberType) {
        company_profile.setText(getAddress);
        email_profile.setText(getPrimaryEmailAddress);
        mobile_profile.setText(getMobilePhoneNumber);
        telephone_profile.setText(getWorkPhoneNumberType);
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
