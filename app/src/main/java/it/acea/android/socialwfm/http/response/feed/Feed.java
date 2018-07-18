package it.acea.android.socialwfm.http.response.feed;

import java.io.Serializable;

import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.http.response.utils.Support;

/**
 * Created by Raphael on 04/11/2015.
 */
public class Feed implements Serializable {

    private String Id;
    private String Title;
    private String Text;
    private String TextWithPlaceholders;
    private String Action;
    private String ActionOnly;
    private String ActionWithPlaceholders;
    private String CreatedAt;
    private boolean Liked;
    private int LikesCount;
    private int RepliesCount;
    private boolean CanDelete;
    private boolean CanReply;
    private Support PreviewImage;
    private Profile Creator;
    private String WebURL;
    private boolean Read;
    private boolean Bookmarked;
    private boolean CanLike;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getTextWithPlaceholders() {
        return TextWithPlaceholders;
    }

    public void setTextWithPlaceholders(String textWithPlaceholders) {
        TextWithPlaceholders = textWithPlaceholders;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getActionOnly() {
        return ActionOnly;
    }

    public void setActionOnly(String actionOnly) {
        ActionOnly = actionOnly;
    }

    public String getActionWithPlaceholders() {
        return ActionWithPlaceholders;
    }

    public void setActionWithPlaceholders(String actionWithPlaceholders) {
        ActionWithPlaceholders = actionWithPlaceholders;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public boolean isLiked() {
        return Liked;
    }

    public void setLiked(boolean liked) {
        Liked = liked;
    }

    public int getLikesCount() {
        return LikesCount;
    }

    public void setLikesCount(int likesCount) {
        LikesCount = likesCount;
    }

    public int getRepliesCount() {
        return RepliesCount;
    }

    public void setRepliesCount(int repliesCount) {
        RepliesCount = repliesCount;
    }

    public boolean isCanDelete() {
        return CanDelete;
    }

    public void setCanDelete(boolean canDelete) {
        CanDelete = canDelete;
    }

    public boolean isCanReply() {
        return CanReply;
    }

    public void setCanReply(boolean canReply) {
        CanReply = canReply;
    }

    public Support getPreviewImage() {
        return PreviewImage;
    }

    public void setPreviewImage(Support previewImage) {
        PreviewImage = previewImage;
    }

    public Profile getCreator() {
        return Creator;
    }

    public void setCreator(Profile creator) {
        Creator = creator;
    }

    public String getWebURL() {
        return WebURL;
    }

    public void setWebURL(String webURL) {
        WebURL = webURL;
    }

    public boolean isRead() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
    }

    public boolean isBookmarked() {
        return Bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        Bookmarked = bookmarked;
    }

    public boolean isCanLike() {
        return CanLike;
    }

    public void setCanLike(boolean canLike) {
        CanLike = canLike;
    }
}
