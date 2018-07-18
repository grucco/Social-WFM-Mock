package it.acea.android.socialwfm.http.user.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Results implements Serializable {

    @SerializedName("__metadata")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.Metadata Metadata;
    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("FirstName")
    @Expose
    private String FirstName;
    @SerializedName("LastName")
    @Expose
    private String LastName;
    @SerializedName("Nickname")
    @Expose
    private String Nickname;
    @SerializedName("Title")
    @Expose
    private String Title;
    @SerializedName("Email")
    @Expose
    private String Email;
    @SerializedName("FullName")
    @Expose
    private String FullName;
    @SerializedName("Role")
    @Expose
    private String Role;
    @SerializedName("IsFollowing")
    @Expose
    private boolean IsFollowing;
    @SerializedName("FeedEntries")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.FeedEntries FeedEntries;
    @SerializedName("ProfilePhoto")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.ProfilePhoto ProfilePhoto;
    @SerializedName("Following")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.Following Following;
    @SerializedName("Followers")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.Followers Followers;
    @SerializedName("ThumbnailImage")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.ThumbnailImage ThumbnailImage;
    @SerializedName("MemberProfile")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.MemberProfile MemberProfile;
    @SerializedName("Managers")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.Managers Managers;
    @SerializedName("DirectReports")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.DirectReports DirectReports;
    @SerializedName("Assistants")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.Assistants Assistants;
    @SerializedName("MemberKudos")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.MemberKudos MemberKudos;

    /**
     * @return The Metadata
     */
    public it.acea.android.socialwfm.http.user.bean.Metadata getMetadata() {
        return Metadata;
    }

    /**
     * @param Metadata The __metadata
     */
    public void setMetadata(it.acea.android.socialwfm.http.user.bean.Metadata Metadata) {
        this.Metadata = Metadata;
    }

    /**
     * @return The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param Id The Id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * @return The FirstName
     */
    public String getFirstName() {
        return FirstName;
    }

    /**
     * @param FirstName The FirstName
     */
    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    /**
     * @return The LastName
     */
    public String getLastName() {
        return LastName;
    }

    /**
     * @param LastName The LastName
     */
    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    /**
     * @return The Nickname
     */
    public String getNickname() {
        return Nickname;
    }

    /**
     * @param Nickname The Nickname
     */
    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }

    /**
     * @return The Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @param Title The Title
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    /**
     * @return The Email
     */
    public String getEmail() {
        return Email;
    }

    /**
     * @param Email The Email
     */
    public void setEmail(String Email) {
        this.Email = Email;
    }

    /**
     * @return The FullName
     */
    public String getFullName() {
        return FullName;
    }

    /**
     * @param FullName The FullName
     */
    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    /**
     * @return The Role
     */
    public String getRole() {
        return Role;
    }

    /**
     * @param Role The Role
     */
    public void setRole(String Role) {
        this.Role = Role;
    }

    /**
     * @return The IsFollowing
     */
    public boolean isIsFollowing() {
        return IsFollowing;
    }

    /**
     * @param IsFollowing The IsFollowing
     */
    public void setIsFollowing(boolean IsFollowing) {
        this.IsFollowing = IsFollowing;
    }

    /**
     * @return The FeedEntries
     */
    public it.acea.android.socialwfm.http.user.bean.FeedEntries getFeedEntries() {
        return FeedEntries;
    }

    /**
     * @param FeedEntries The FeedEntries
     */
    public void setFeedEntries(it.acea.android.socialwfm.http.user.bean.FeedEntries FeedEntries) {
        this.FeedEntries = FeedEntries;
    }

    /**
     * @return The ProfilePhoto
     */
    public it.acea.android.socialwfm.http.user.bean.ProfilePhoto getProfilePhoto() {
        return ProfilePhoto;
    }

    /**
     * @param ProfilePhoto The ProfilePhoto
     */
    public void setProfilePhoto(it.acea.android.socialwfm.http.user.bean.ProfilePhoto ProfilePhoto) {
        this.ProfilePhoto = ProfilePhoto;
    }

    /**
     * @return The Following
     */
    public it.acea.android.socialwfm.http.user.bean.Following getFollowing() {
        return Following;
    }

    /**
     * @param Following The Following
     */
    public void setFollowing(it.acea.android.socialwfm.http.user.bean.Following Following) {
        this.Following = Following;
    }

    /**
     * @return The Followers
     */
    public it.acea.android.socialwfm.http.user.bean.Followers getFollowers() {
        return Followers;
    }

    /**
     * @param Followers The Followers
     */
    public void setFollowers(it.acea.android.socialwfm.http.user.bean.Followers Followers) {
        this.Followers = Followers;
    }

    /**
     * @return The ThumbnailImage
     */
    public it.acea.android.socialwfm.http.user.bean.ThumbnailImage getThumbnailImage() {
        return ThumbnailImage;
    }

    /**
     * @param ThumbnailImage The ThumbnailImage
     */
    public void setThumbnailImage(it.acea.android.socialwfm.http.user.bean.ThumbnailImage ThumbnailImage) {
        this.ThumbnailImage = ThumbnailImage;
    }

    /**
     * @return The MemberProfile
     */
    public it.acea.android.socialwfm.http.user.bean.MemberProfile getMemberProfile() {
        return MemberProfile;
    }

    /**
     * @param MemberProfile The MemberProfile
     */
    public void setMemberProfile(it.acea.android.socialwfm.http.user.bean.MemberProfile MemberProfile) {
        this.MemberProfile = MemberProfile;
    }

    /**
     * @return The Managers
     */
    public it.acea.android.socialwfm.http.user.bean.Managers getManagers() {
        return Managers;
    }

    /**
     * @param Managers The Managers
     */
    public void setManagers(it.acea.android.socialwfm.http.user.bean.Managers Managers) {
        this.Managers = Managers;
    }

    /**
     * @return The DirectReports
     */
    public it.acea.android.socialwfm.http.user.bean.DirectReports getDirectReports() {
        return DirectReports;
    }

    /**
     * @param DirectReports The DirectReports
     */
    public void setDirectReports(it.acea.android.socialwfm.http.user.bean.DirectReports DirectReports) {
        this.DirectReports = DirectReports;
    }

    /**
     * @return The Assistants
     */
    public it.acea.android.socialwfm.http.user.bean.Assistants getAssistants() {
        return Assistants;
    }

    /**
     * @param Assistants The Assistants
     */
    public void setAssistants(it.acea.android.socialwfm.http.user.bean.Assistants Assistants) {
        this.Assistants = Assistants;
    }

    /**
     * @return The MemberKudos
     */
    public it.acea.android.socialwfm.http.user.bean.MemberKudos getMemberKudos() {
        return MemberKudos;
    }

    /**
     * @param MemberKudos The MemberKudos
     */
    public void setMemberKudos(it.acea.android.socialwfm.http.user.bean.MemberKudos MemberKudos) {
        this.MemberKudos = MemberKudos;
    }

}
