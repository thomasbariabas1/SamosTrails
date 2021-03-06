package gr.aegean.com.samostrails.Models;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import gr.aegean.com.samostrails.R;

public class Trail implements Parcelable {

    public long DBId=0;
    private boolean Children_Friedly = false;
    private int TrailId;
    public DifficultyLevel DifficultyLevel;
    public DistanceLevel DistanceLevel;
    public KindOfTrail KindOfTrail;
    private String image;
    private String GeometryCollection;
    private double Distance;
    private String Title;
    private Bitmap downlImage;
    private String ConnectionToOtherTrails;
    private String Description;
    private String MainSights;
    private String OtherTransport;
    private String StrartingPoin;
    private String Tips;
    private String Video;
    private boolean editable;
    private String url;

    public Trail(boolean children_Friedly, int tailid, DifficultyLevel difficultyLevel,
                 gr.aegean.com.samostrails.Models.DistanceLevel distanceLevel,
                 gr.aegean.com.samostrails.Models.KindOfTrail kindOfTrail,
                 String image, String geometryCollection,
                 double distance, String title, String ConnectionToOtherTrails, String Description, String MainSights, String OtherTransport,
                 String StrartingPoin, String Tips, String Video, String url) {
        Children_Friedly = children_Friedly;
        TrailId = tailid;
        DifficultyLevel = difficultyLevel;
        DistanceLevel = distanceLevel;
        KindOfTrail = kindOfTrail;
        this.image = image;
        GeometryCollection = geometryCollection;
        Distance = distance;
        Title = title;
        this.ConnectionToOtherTrails = ConnectionToOtherTrails;
        this.Description = Description;
        this.MainSights = MainSights;
        this.OtherTransport = OtherTransport;
        this.StrartingPoin = StrartingPoin;
        this.Tips = Tips;
        this.Video = Video;
        this.editable=false;
        this.url=url;
    }
    public Trail(boolean children_Friedly, int tailid, DifficultyLevel difficultyLevel,
                 gr.aegean.com.samostrails.Models.DistanceLevel distanceLevel,
                 gr.aegean.com.samostrails.Models.KindOfTrail kindOfTrail,
                 String image, String geometryCollection,
                 double distance, String title, String ConnectionToOtherTrails, String Description, String MainSights, String OtherTransport,
                 String StrartingPoin, String Tips, String Video,boolean editable) {
        Children_Friedly = children_Friedly;
        TrailId = tailid;
        DifficultyLevel = difficultyLevel;
        DistanceLevel = distanceLevel;
        KindOfTrail = kindOfTrail;
        this.image = image;
        GeometryCollection = geometryCollection;
        Distance = distance;
        Title = title;
        this.ConnectionToOtherTrails = ConnectionToOtherTrails;
        this.Description = Description;
        this.MainSights = MainSights;
        this.OtherTransport = OtherTransport;
        this.StrartingPoin = StrartingPoin;
        this.Tips = Tips;
        this.Video = Video;
        this.editable=editable;
    }
    public Trail(Context context) {
        Children_Friedly = false;
        TrailId = 0;
        DifficultyLevel = gr.aegean.com.samostrails.Models.DifficultyLevel.NotSelected;
        DistanceLevel =  gr.aegean.com.samostrails.Models.DistanceLevel.NotSelected;
        KindOfTrail =  gr.aegean.com.samostrails.Models.KindOfTrail.NotSelected;
        this.image = "";
        GeometryCollection = "";
        Distance = 0;
        Title = "";
        this.ConnectionToOtherTrails = "";
        this.Description = "";
        this.MainSights = "";
        this.OtherTransport = "";
        this.StrartingPoin = "";
        this.Tips = "";
        this.Video = "";
        this.downlImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_trail_image);
        this.editable=true;
    }

    public boolean getChildrenFriendly() {
        return this.Children_Friedly;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public void setConnectionToOtherTrails(String connectionToOtherTrails) {
        ConnectionToOtherTrails = connectionToOtherTrails;
    }

    public long getDBId() {
        return DBId;
    }

    public void setDBId(long DBId) {
        this.DBId = DBId;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setMainSights(String mainSights) {
        MainSights = mainSights;
    }

    public void setOtherTransport(String otherTransport) {
        OtherTransport = otherTransport;
    }

    public void setStrartingPoin(String strartingPoin) {
        StrartingPoin = strartingPoin;
    }

    public void setTips(String tips) {
        Tips = tips;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public String getConnectionToOtherTrails() {
        return ConnectionToOtherTrails;
    }

    public String getDescription() {
        return Description;
    }

    public String getMainSights() {
        return MainSights;
    }

    public String getOtherTransport() {
        return OtherTransport;
    }

    public String getStrartingPoin() {
        return StrartingPoin;
    }

    public String getTips() {
        return Tips;
    }

    public String getVideo() {
        return Video;
    }

    public String getTitle() {

        return Title;
    }


    public boolean isEditable() {
        return editable;
    }
    public void setDownlImage(Bitmap downlImage) {
        this.downlImage = downlImage;
    }

    public Bitmap getDownlImage() {
        return downlImage;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public boolean isChildren_Friedly() {
        return Children_Friedly;
    }

    public DifficultyLevel getDifficultyLevel() {
        return DifficultyLevel;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public double getDistance() {
        return Distance;
    }

    public DistanceLevel getDistanceLevel() {
        return DistanceLevel;
    }

    public KindOfTrail getKindOfTrail() {
        return KindOfTrail;
    }

    public String getImage() {
        return image;
    }

    public String getGeometryCollection() {
        return GeometryCollection;
    }

    public void setChildren_Friedly(boolean children_Friedly) {
        Children_Friedly = children_Friedly;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        DifficultyLevel = difficultyLevel;
    }

    public void setDistanceLevel(DistanceLevel distanceLevel) {
        DistanceLevel = distanceLevel;
    }

    public void setKindOfTrail(KindOfTrail kindOfTrail) {
        KindOfTrail = kindOfTrail;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setGeometryCollection(String geometryCollection) {
        GeometryCollection = geometryCollection;
    }

    public int getTrailId() {
        return TrailId;
    }

    public void setTrailId(int trailId) {
        TrailId = trailId;
    }


    //Parceble----

    protected Trail(Parcel in) {
        Children_Friedly = in.readByte() != 0x00;
        TrailId = in.readInt();
        DifficultyLevel = (DifficultyLevel) in.readValue(DifficultyLevel.class.getClassLoader());
        DistanceLevel = (DistanceLevel) in.readValue(DistanceLevel.class.getClassLoader());
        KindOfTrail = (KindOfTrail) in.readValue(KindOfTrail.class.getClassLoader());
        image = in.readString();
        GeometryCollection = in.readString();
        Distance = in.readDouble();
        Title = in.readString();
        downlImage = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        ConnectionToOtherTrails = in.readString();
        Description = in.readString();
        MainSights = in.readString();
        OtherTransport = in.readString();
        StrartingPoin = in.readString();
        Tips = in.readString();
        Video = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (Children_Friedly ? 0x01 : 0x00));
        dest.writeInt(TrailId);
        dest.writeValue(DifficultyLevel);
        dest.writeValue(DistanceLevel);
        dest.writeValue(KindOfTrail);
        dest.writeString(image);
        dest.writeString(GeometryCollection);
        dest.writeDouble(Distance);
        dest.writeString(Title);
        dest.writeValue(downlImage);
        dest.writeString(ConnectionToOtherTrails);
        dest.writeString(Description);
        dest.writeString(MainSights);
        dest.writeString(OtherTransport);
        dest.writeString(StrartingPoin);
        dest.writeString(Tips);
        dest.writeString(Video);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trail> CREATOR = new Parcelable.Creator<Trail>() {
        @Override
        public Trail createFromParcel(Parcel in) {
            return new Trail(in);
        }

        @Override
        public Trail[] newArray(int size) {
            return new Trail[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Trail{" +
                "DBId=" + DBId +
                ", Children_Friedly=" + Children_Friedly +
                ", TrailId=" + TrailId +
                ", DifficultyLevel=" + DifficultyLevel +
                ", DistanceLevel=" + DistanceLevel +
                ", KindOfTrail=" + KindOfTrail +
                ", image='" + image + '\'' +
                ", GeometryCollection='" + "" + '\'' +
                ", Distance=" + Distance +
                ", Title='" + Title + '\'' +
                ", downlImage=" + downlImage +
                ", ConnectionToOtherTrails='" + ConnectionToOtherTrails + '\'' +
                ", Description='" + Description + '\'' +
                ", MainSights='" + MainSights + '\'' +
                ", OtherTransport='" + OtherTransport + '\'' +
                ", StrartingPoin='" + StrartingPoin + '\'' +
                ", Tips='" + Tips + '\'' +
                ", Video='" + Video + '\'' +
                ", editable=" + editable +
                ", url='" + url + '\'' +
                '}';
    }
}


