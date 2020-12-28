package com.example.iffath.icu.Model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact implements Parcelable {
    private int id;
    private String name;
    private String nickname;
    private String email;
    private int phone;
    private int account_id;

    protected EmergencyContact(Parcel in) {
        id = in.readInt();
        name = in.readString();
        nickname = in.readString();
        email = in.readString();
        phone = in.readInt();
        account_id = in.readInt();
    }

    public static final Creator<EmergencyContact> CREATOR = new Creator<EmergencyContact>() {
        @Override
        public EmergencyContact createFromParcel(Parcel in) {
            return new EmergencyContact(in);
        }

        @Override
        public EmergencyContact[] newArray(int size) {
            return new EmergencyContact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(nickname);
        parcel.writeString(email);
        parcel.writeInt(phone);
        parcel.writeInt(account_id);
    }
}
