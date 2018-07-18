package it.acea.android.socialwfm.factory;

import android.content.Context;
import android.text.TextUtils;

import org.fingerlinks.mobile.android.utils.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import it.acea.android.socialwfm.app.model.odl.Customer;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.http.odl.Cliente;
import it.acea.android.socialwfm.http.odl.Impianto;
import it.acea.android.socialwfm.http.odl.ODLBean;

/**
 * Created by fabio on 18/11/15.
 */
public class ODLProcessFactory {

    private Context mContext;
    private ODLBean[] mOdlToProcess;
    private static ODLProcessFactory mInstance;

    ODLProcessFactory(Context context) {
        mContext = context;
    }

    public static ODLProcessFactory with(Context context) {
        mInstance = new ODLProcessFactory(context);
        return mInstance;
    }

    public ODLProcessFactory odlBeans(List<ODLBean> odls) {
        if(odls != null){
            mOdlToProcess = odls.toArray(new ODLBean[odls.size()]);
        }
        return mInstance;
    }

    public ODLProcessFactory odlBeans(ODLBean... odl) {
        mOdlToProcess = odl;
        return mInstance;
    }

    public void process() {
        if (mOdlToProcess == null || mOdlToProcess.length==0) {
            //Cancello tutti i dati relativi agli ordini di lavoro
            UserHelperFactory.brasaOdl();
        }
        else{
            mergeOdl(mOdlToProcess);
        }
    }

    /**
     * Confronta gli odl presenti sul database e quelli restituiti contenuti in toProcess
     * . I nuovi ordini sono aggiunti
     * . Gli ordini già presenti sono ignorati (almeno mi risparmio il reverse geocoding)
     * . Gli ordini che non sono i toProcess vengono rimossi
     * */
    private void mergeOdl(ODLBean[] toProcess){
        // Leggo la lista presente sul database
        Realm realm = Realm.getDefaultInstance();
        List<Odl> odlList = realm.where(Odl.class).findAll();

        List<String> toBeRemoved = new ArrayList<String>();

        //Rimuovo tutti gli ordini che non sono in toProcess ma sono nel DB locale
        for(Odl odl:odlList) {
            boolean toRemove = true;
            for (ODLBean odlBean:toProcess) {
                String customKey = odlBean.getAufnr() + odlBean.getVornr();
                if(odl.getCustomKey().equals(customKey)){
                    toRemove = false;
                    break;
                }
            }
            if(toRemove){
                toBeRemoved.add(odl.getCustomKey());
            }
        }

        for(String odlKey:toBeRemoved){
            deleteFromRealm(odlKey);
        }

        //Aggiungo al DB gli ordini che sono solo in toProcess ma non in locale
        for(ODLBean odlBean:toProcess) {
            String customKey = odlBean.getAufnr() + odlBean.getVornr();
            Odl odl = realm.where(Odl.class).equalTo("customKey",customKey).findFirst();
            if(odl == null){
                insertOnRealm(odlBean);
            }
        }

    }


    private void deleteFromRealm(String odlKey) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Odl odl = realm.where(Odl.class)
                .equalTo("customKey", odlKey)
                .findFirst();
        if (odl != null) odl.removeFromRealm();
        realm.commitTransaction();
    }

    /**
     * execute insert into realm
     *
     * @param bean
     */
    private void insertOnRealm(ODLBean bean) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Odl odl = buildBaseOdl(bean);
        /**
         * Customer
         */
        Cliente cliente = bean.getCliente();
        if (cliente != null && !TextUtils.isEmpty(cliente.getBp()) && !cliente.getBp().equalsIgnoreCase("--")) {
            Customer customer = buildCustomer(cliente);
            odl.setCustomer(customer);
        }

        Impianto impianto = bean.getImpianto();
        if (impianto != null) {
            Plant plant = buildPlant(impianto);
            odl.setPlant(plant);
        }
        realm.copyToRealmOrUpdate(odl);
        realm.commitTransaction();
    }

    private void updateRealmObject(ODLBean bean) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Odl odl = realm.where(Odl.class)
                .equalTo("customKey",
                        bean.getAufnr() + bean.getVornr())
                .findFirst();

        odl = buildBaseOdl(odl, bean);

        Cliente cliente = bean.getCliente();
        if (cliente != null && !TextUtils.isEmpty(cliente.getBp()) && !cliente.getBp().equalsIgnoreCase("--")) {
            Customer customer = buildCustomer(cliente);
            odl.setCustomer(customer);
        }

        Impianto impianto = bean.getImpianto();
        if (impianto != null) {
            Plant plant = buildPlant(impianto);
            odl.setPlant(plant);
        }
        realm.copyToRealmOrUpdate(odl);
        realm.commitTransaction();
    }

    private Odl buildBaseOdl(ODLBean odlBean) {
        return buildBaseOdl(null, odlBean);
    }

    private Odl buildBaseOdl(Odl _odl, ODLBean odlBean) {
        Odl odl = (_odl == null) ? new Odl() : _odl;
        odl.setCustomKey(odlBean.getAufnr() + odlBean.getVornr());
        odl.setAufnr(odlBean.getAufnr());
        odl.setVornr(odlBean.getVornr());
        odl.setPernr(odlBean.getVornr());
        odl.setOdltype(odlBean.getOdltype());
        odl.setTaskdesc(odlBean.getTaskdesc());
        odl.setDatetime(odlBean.getOra());
        odl.setUser(odlBean.getUser());

        odl.setApp(TextUtils.isEmpty(odlBean.getIsApp()) ? false : true);
        odl.setTeam(TextUtils.isEmpty(odlBean.getIsTeam()) ? false : true);
        odl.setPreposto(TextUtils.isEmpty(odlBean.getIsPreposto()) ? false : true);

        odl.setDeltaStatus(odlBean.getDeltaStatus());
        odl.setStatus(odlBean.getStatus());
        return odl;
    }

    private Customer buildCustomer(Cliente cliente) {
        Customer customer = new Customer();
        customer.setBp(cliente.getBp());
        customer.setTariftype(cliente.getCondcontract());
        customer.setCondcontract(cliente.getCondcontract());
        customer.setRuoloutenza(cliente.getRuoloutenza());
        customer.setCondpagamento(cliente.getCondpagamento());
        customer.setDetcosti(cliente.getDetcosti());
        customer.setTiputenza(cliente.getTiputenza());
        customer.setStreet(cliente.getStreet());
        customer.setHousenum(cliente.getHousenum());
        customer.setRegion(cliente.getRegion());
        customer.setPostcode(cliente.getPostcode());
        customer.setEsigspecial(cliente.getEsigspecial());
        customer.setNome(cliente.getNome());
        customer.setCognome(cliente.getCognome());
        customer.setMobphone(cliente.getMobphone());
        customer.setCellphone(cliente.getCellphone());
        customer.setFax(cliente.getFax());
        customer.setEmail(cliente.getEmail());
        customer.setRole(cliente.getRole());
        customer.setBpstatus(cliente.getBpstatus());
        customer.setCity(cliente.getCity());
        customer.setImpscad(cliente.getImpscad());
        customer.setUtencoin(cliente.getUtencoin());
        customer.setImpmor(cliente.getImpmor());
        customer.setFullName(cliente.getFullName());
        customer.setFullAddress(cliente.getFullAddress());

        /**
         * custom da gestire
         */
        try {
            if (cliente.getDispAgg() != null && cliente.getDispAgg().getResults() != null && !cliente.getDispAgg().getResults().isEmpty()) {
                String toFormatHtmlValue = "";
                for (int i = 0; i < cliente.getDispAgg().getResults().size(); i++) {
                    String _value = cliente.getDispAgg().getResults().get(i).getDispagg();
                    toFormatHtmlValue += _value + "</br>";
                }
                customer.setDispAgg(toFormatHtmlValue);
            }
        } catch (Exception _ex) {
        }

        return customer;
    }

    private Plant buildPlant(Impianto impianto) {
        Plant plant = new Plant();
        plant.setAnlage(impianto.getAnlage());

        plant.setSedetec(impianto.getSedetec());
        plant.setRuoloutenza(impianto.getRuoloutenza());
        plant.setDescappar(impianto.getDescappar());
        plant.setScala(impianto.getScala());
        plant.setPiano(impianto.getPiano());
        plant.setInterno(impianto.getInterno());
        plant.setDefsedetec(impianto.getDefsedetec());
        plant.setLocalita(impianto.getLocalita());
        plant.setMunicipio(impianto.getMunicipio());
        plant.setCap(impianto.getCap());
        plant.setVia(impianto.getVia());
        plant.setCivico(impianto.getCivico());
        plant.setProvincia(impianto.getProvincia());
        plant.setUbicazione(impianto.getUbicazione());
        plant.setCategoria(impianto.getCategoria());
        plant.setProfilo(impianto.getProfilo());
        plant.setLongitude(impianto.getLongitude());
        plant.setLatitude(impianto.getLatitude());
        plant.setPrecisione(impianto.getPrecisione());
        plant.setAddressComplete(impianto.getAddressComplete());

        plant.setFanghi(TextUtils.isEmpty(impianto.getIsFanghi()) ? false : true);
        plant.setCloro(TextUtils.isEmpty(impianto.getIsCloro()) ? false : true);
        plant.setChimic(TextUtils.isEmpty(impianto.getIsChimic()) ? false : true);
        plant.setQuadEl(TextUtils.isEmpty(impianto.getIsQuadEl()) ? false : true);
        plant.setElmecc(TextUtils.isEmpty(impianto.getIsElmecc()) ? false : true);
        plant.setVideosorv(TextUtils.isEmpty(impianto.getIsVideosorv()) ? false : true);
        plant.setMisto(TextUtils.isEmpty(impianto.getIsMisto()) ? false : true);

        plant.setSettIndRif(impianto.getSettIndRif());
        plant.setSettIndFath(impianto.getSettIndFath());
        plant.setDefsedetecFat(impianto.getDefsedetecFat());

        return plant;
    }

    private String TAG = ODLProcessFactory.class.getName();
};