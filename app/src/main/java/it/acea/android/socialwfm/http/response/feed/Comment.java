package it.acea.android.socialwfm.http.response.feed;

import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.http.response.utils.Support;

/**
 * Created by Raphael on 09/11/2015.
 */
public class Comment {

    private String Id;
    private String CreatedAt;
    private String LastModifiedAt;
    private String Text;
    private String TextWithPlaceholders;
    private boolean Liked;
    private int LikesCount;
    private boolean CanDelete;
    private Profile Creator;
    private Support ThumbnailImage;
    private AtMantions AtMentions;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getLastModifiedAt() {
        return LastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        LastModifiedAt = lastModifiedAt;
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

    public boolean isCanDelete() {
        return CanDelete;
    }

    public void setCanDelete(boolean canDelete) {
        CanDelete = canDelete;
    }

    public Profile getCreator() {
        return Creator;
    }

    public void setCreator(Profile creator) {
        Creator = creator;
    }

    public Support getThumbnailImage() {
        return ThumbnailImage;
    }

    public void setThumbnailImage(Support thumbnailImage) {
        ThumbnailImage = thumbnailImage;
    }

    public AtMantions getAtMentions() {
        return AtMentions;
    }

    public void setAtMentions(AtMantions atMentions) {
        AtMentions = atMentions;
    }
}
