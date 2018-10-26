/**
 * Copyright 2018 Iron City Software LLC
 *
 * This file is part of DoorKing.
 *
 * DoorKing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DoorKing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DoorKing.  If not, see <http://www.gnu.org/licenses/>.
 */

package doorking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

/**
 * A DoorKing Entry which will be written to a CSV file for import
 * into Account Manager.
 * TODO: improve validation
 */
public class Entry {
  private final String directoryDisplayName;
  private final boolean isHidden;
  private final String areaCode;
  private final String phoneNumber;
  private final Integer directoryNumber;
  private final Integer entryCode;
  private final Integer securityLevel;
  private final String deviceNumber;
  private final String notes;
  private final boolean isVendor;

  private Entry(String directoryDisplayName,
      boolean isHidden,
      String areaCode,
      String phoneNumber,
      Integer directoryNumber,
      Integer entryCode,
      Integer securityLevel,
      String deviceNumber,
      String notes,
      boolean isVendor) {
    Preconditions.checkArgument(!(entryCode == null ^ securityLevel == null));
    this.directoryDisplayName = directoryDisplayName;
    this.isHidden = isHidden;
    this.areaCode = areaCode;
    this.phoneNumber = phoneNumber;
    this.directoryNumber = directoryNumber;
    this.entryCode = entryCode;
    this.securityLevel = securityLevel;
    this.deviceNumber = deviceNumber;
    this.notes = notes;
    this.isVendor = isVendor;
  }

  @Override
  public int hashCode() {
    return Objects.hash(directoryDisplayName, isHidden, areaCode, phoneNumber,
        directoryNumber, entryCode, securityLevel, deviceNumber, notes, isVendor);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Entry)) {
      return false;
    }
    Entry that = (Entry) obj;
    return Objects.equals(this.directoryDisplayName, that.directoryDisplayName)
        && Objects.equals(this.isHidden, that.isHidden)
        && Objects.equals(this.areaCode, that.areaCode)
        && Objects.equals(this.phoneNumber, that.phoneNumber)
        && Objects.equals(this.directoryNumber, that.directoryNumber)
        && Objects.equals(this.entryCode, that.entryCode)
        && Objects.equals(this.securityLevel, that.securityLevel)
        && Objects.equals(this.deviceNumber, that.deviceNumber)
        && Objects.equals(this.notes, that.notes)
        && Objects.equals(this.isVendor, that.isVendor);
  }

  private static final Joiner COMMA_JOINER = Joiner.on(',');

  private static final String HEADERS[] = { "Resident", "H", "AAC", "PHONE",
      "DIR", "ENT", "SL", "DEVICE#", "NOTES", "VENDOR" };

  public static String getHeaders() {
    return COMMA_JOINER.join(HEADERS);
  }

  @Override
  public String toString() {
    List<String> components = new ArrayList<>();
    components.add(directoryDisplayName == null ? "" : directoryDisplayName);
    components.add(isHidden ? "Y" : "N");
    components.add(areaCode == null ? "" : "1" + areaCode);
    components.add(phoneNumber == null ? "" : phoneNumber);
    components.add(directoryNumber == null ? "" : String.format("%03d", directoryNumber));
    components.add(entryCode == null ? "" : String.format("%04d", entryCode));
    components.add(securityLevel == null ? "" : String.format("%02d", securityLevel));
    components.add(deviceNumber == null ? "" : deviceNumber);
    components.add(notes == null ? "" : notes);
    components.add(isVendor ? "Y" : "N");
    return COMMA_JOINER.join(components);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {
    }

    String directoryDisplayName;
    boolean isHidden;
    String areaCode;
    String phoneNumber;
    Integer directoryNumber;
    Integer entryCode;
    Integer securityLevel;
    String deviceNumber;
    String notes;
    boolean isVendor;

    public Builder setDirectoryDisplayName(String directoryDisplayName) {
      this.directoryDisplayName = directoryDisplayName;
      return this;
    }

    public String getDirectoryDisplayName() {
      return directoryDisplayName;
    }

    public Builder markHidden() {
      this.isHidden = true;
      return this;
    }

    public Builder setAreaCode(String areaCode) {
      this.areaCode = areaCode;
      return this;
    }

    public Builder clearAreaCode() {
      this.areaCode = null;
      return this;
    }

    public Builder setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public Builder clearPhoneNumber() {
      this.phoneNumber = null;
      return this;
    }

    public Builder clearDirectoryNumber() {
      this.directoryNumber = null;
      return this;
    }

    public Builder setDirectoryNumber(int directoryNumber) {
      this.directoryNumber = directoryNumber;
      return this;
    }

    public Builder setEntryCode(int entryCode) {
      this.entryCode = entryCode;
      return this;
    }

    public Builder setSecurityLevel(int securityLevel) {
      this.securityLevel = securityLevel;
      return this;
    }

    public Builder setDeviceNumber(String deviceNumber) {
      this.deviceNumber = deviceNumber;
      return this;
    }
    
    public Builder clearDeviceNumber() {
      this.deviceNumber = null;
      return this;
    }

    public Builder setNotes(String notes) {
      this.notes = notes;
      return this;
    }

    public Builder markVendor() {
      this.isVendor = true;
      return this;
    }

    public Entry build() {
      return new Entry(directoryDisplayName, isHidden, areaCode, phoneNumber,
          directoryNumber, entryCode, securityLevel, deviceNumber, notes, isVendor);
    }
  }
}
