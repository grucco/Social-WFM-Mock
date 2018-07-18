package it.acea.android.socialwfm.app.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.mikepenz.iconics.IconicsDrawable;

import it.acea.android.socialwfm.R;
import tr.xip.errorview.ErrorView;

/**
 * Created by nicola on 21/10/16.
 */

public class ErrorViewManager {

    public static ErrorView getErrorViewNoData(Context context) {
        ErrorView errorView = new ErrorView(context);
        setErrorViewNoData(errorView);
        return errorView;
    }

    public static ErrorView getErrorViewNoConnection(Context context) {
        ErrorView errorView = new ErrorView(context);
        setErrorViewNoConnection(errorView);
        return errorView;
    }

    public static void setErrorViewNoData(ErrorView errorView) {
        IconicsDrawable frown = new IconicsDrawable(errorView.getContext())
                .icon("faw-frown-o")
                .sizeDp(70)
                .color(ContextCompat.getColor(errorView.getContext(), R.color.md_blue_grey_300));
        errorView.setImage(frown);
        errorView.setSubtitle(R.string.no_results_found);
        errorView.setRetryButtonText(R.string.empty_view_aggiorna);
    }

    public static void setErrorViewNoConnection(ErrorView errorView) {
        IconicsDrawable frown = new IconicsDrawable(errorView.getContext())
                .icon("faw-frown-o")
                .sizeDp(70)
                .color(ContextCompat.getColor(errorView.getContext(), R.color.md_blue_grey_300));
        errorView.setImage(frown);
        errorView.setTitle(R.string.connection_error);
        errorView.setSubtitle(R.string.error_message_network);
        errorView.showRetryButton(true);
        errorView.setRetryButtonText(R.string.empty_view_riprova);
    }

    public static void setErrorViewNoOdl(ErrorView errorView) {
        IconicsDrawable smile = new IconicsDrawable(errorView.getContext())
                .icon("faw-smile-o")
                .sizeDp(70)
                .color(ContextCompat.getColor(errorView.getContext(), R.color.md_blue_grey_300));
        errorView.setImage(smile);
        errorView.setTitle(R.string.ottimo);
        errorView.setSubtitle(R.string.non_hai_ordini_di_lavoro);
        errorView.setRetryButtonText(R.string.empty_view_riprova);
    }

    public static void setErrorViewNoDocuments(ErrorView errorView) {
        IconicsDrawable file = new IconicsDrawable(errorView.getContext())
                .icon("gmi-file-text")
                .sizeDp(70)
                .color(ContextCompat.getColor(errorView.getContext(), R.color.md_blue_grey_300));
        errorView.setImage(file);
        errorView.setTitle(R.string.group_media_no_data);
        errorView.showRetryButton(false);
        errorView.showSubtitle(false);
    }

    public static void setErrorViewNoPlantFeeds(ErrorView errorView) {
        IconicsDrawable pencil = new IconicsDrawable(errorView.getContext())
                .icon("faw-pencil")
                .sizeDp(70)
                .color(ContextCompat.getColor(errorView.getContext(), R.color.md_blue_grey_300));
        errorView.setImage(pencil);
        errorView.setTitle(R.string.non_ci_sono_post);
        errorView.setSubtitle(R.string.scrivi_il_primo_post_su_questo_impianto);
    }

    public static void setErrorViewNoGroups(ErrorView errorView) {
        IconicsDrawable accounts = new IconicsDrawable(errorView.getContext())
                .icon("gmi-accounts")
                .sizeDp(70)
                .color(ContextCompat.getColor(errorView.getContext(), R.color.md_blue_grey_300));
        errorView.setImage(accounts);
        errorView.setTitle(R.string.groups_list_no_groups_found);
        errorView.setSubtitle(R.string.groups_list_no_groups_found_description);
        errorView.showRetryButton(false);
    }

    public static void setErrorViewNoContacts(ErrorView errorView) {
        IconicsDrawable account = new IconicsDrawable(errorView.getContext())
                .icon("gmi-account")
                .sizeDp(70)
                .color(ContextCompat.getColor(errorView.getContext(), R.color.md_blue_grey_300));
        errorView.setImage(account);
        errorView.setTitle(R.string.contacts_list_no_contacts_found);
        errorView.setSubtitle(R.string.contacts_list_no_contacts_found_description);
        errorView.showRetryButton(true);
        errorView.setRetryButtonText(R.string.contacts_list_no_contacts_found_update);
    }

    public static void setErrorViewNoComments(ErrorView errorView) {
        IconicsDrawable comment = new IconicsDrawable(errorView.getContext())
                .icon("gmd-comment")
                .sizeDp(70)
                .color(ContextCompat.getColor(errorView.getContext(), R.color.md_blue_grey_300));
        errorView.setImage(comment);
        errorView.setTitle(R.string.comment_dialog_nessun_commento);
        errorView.showSubtitle(false);
        errorView.showRetryButton(false);
    }
}
