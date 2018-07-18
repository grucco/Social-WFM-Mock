package it.acea.android.socialwfm.app.model.ess;

import android.text.TextUtils;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import it.acea.android.socialwfm.Utils;

/**
 * Created by raphaelbussa on 17/03/16.
 */
public class Giustificativi extends RealmObject {
    @Ignore
    public static final String IS_PREPARE_CREATE = "CREATE";

    @Ignore
    public static final String IS_PREPARE_EDIT = "EDIT";

    @Ignore
    public static final String EMPTY_DOC_NR = "00000000000000000000";

    @PrimaryKey
    private String RequestId;

    private String ActionChangeVisibility;
    private String Deduction;
    private String IsPrepare;
    private String AttabsHours;
    private String DeductionTooltip;
    private String ActionCancelVisibility;
    private String Namenxp;
    private String Status;
    private String StatusText;
    private String CurrNotice;
    private String BeginTime;
    private String EndTime;
    private String Subty;
    private String Endda;
    private String Begda;
    private String SubtypeDescription;
    private String DateSearch;
    private String Docnr;

    public String getActionChangeVisibility() {
        return ActionChangeVisibility;
    }

    public void setActionChangeVisibility(String actionChangeVisibility) {
        ActionChangeVisibility = actionChangeVisibility;
    }

    public String getDeduction() {
        return Deduction;
    }

    public void setDeduction(String deduction) {
        Deduction = deduction;
    }

    public String getIsPrepare() {
        return IsPrepare;
    }

    public void setIsPrepare(String isPrepare) {
        IsPrepare = isPrepare;
    }

    public String getAttabsHours() {
        return AttabsHours;
    }

    public void setAttabsHours(String attabsHours) {
        AttabsHours = attabsHours;
    }

    public String getDeductionTooltip() {
        return DeductionTooltip;
    }

    public void setDeductionTooltip(String deductionTooltip) {
        DeductionTooltip = deductionTooltip;
    }

    public String getActionCancelVisibility() {
        return ActionCancelVisibility;
    }

    public void setActionCancelVisibility(String actionCancelVisibility) {
        ActionCancelVisibility = actionCancelVisibility;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getNamenxp() {
        return Namenxp;
    }

    public void setNamenxp(String namenxp) {
        Namenxp = namenxp;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStatusText() {
        return StatusText;
    }

    public void setStatusText(String statusText) {
        StatusText = statusText;
    }

    public String getCurrNotice() {
        return CurrNotice;
    }

    public void setCurrNotice(String currNotice) {
        CurrNotice = currNotice;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getSubty() {
        return Subty;
    }

    public void setSubty(String subty) {
        Subty = subty;
    }

    public String getEndda() {
        return Endda;
    }

    public void setEndda(String endda) {
        Endda = endda;
    }

    public String getBegda() {
        return Begda;
    }


    public static String getDataEOraInizio(Giustificativi giustificativo) {
        Date d = Utils.cleanDateField(giustificativo.getBegda());
        String data = Utils.dateFormat(d);
        String ora = Utils.formatTimePTHMS(giustificativo.getBeginTime());
        return data + " " + ora;
    }

    public static String getDataEOraFine(Giustificativi giustificativo) {
        Date d = Utils.cleanDateField(giustificativo.getEndda());
        String data = Utils.dateFormat(d);
        String ora = Utils.formatTimePTHMS(giustificativo.getEndTime());
        return data + " " + ora;
    }

    public static String getDataInizio(Giustificativi giustificativo) {
        Date d = Utils.cleanDateField(giustificativo.getBegda());
        return Utils.dateFormat(d);
    }

    public static String getDataFine(Giustificativi giustificativo) {
        Date d = Utils.cleanDateField(giustificativo.getEndda());
        return Utils.dateFormat(d);
    }

    public static boolean isGiustificativoSpeciale(Giustificativi giustificativo) {
        return !(TextUtils.isEmpty(giustificativo.getDateSearch()) && giustificativo.getDocnr().equals(EMPTY_DOC_NR));
    }

    public void setBegda(String begda) {
        Begda = begda;
    }

    public String getSubtypeDescription() {
        return SubtypeDescription;
    }

    public void setSubtypeDescription(String subtypeDescription) {
        SubtypeDescription = subtypeDescription;
    }

    public String getDateSearch() {
        return DateSearch;
    }

    public void setDateSearch(String dateSearch) {
        DateSearch = dateSearch;
    }

    public String getDocnr() {
        return Docnr;
    }

    public void setDocnr(String docnr) {
        Docnr = docnr;
    }
}
