package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpadteSoundBoxDetailsRequest {

	private long soundBoxSubscriptionId;

	@NotBlank(message = "soundTId cannot be empty")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Please enter valid soundTId")
	@Size(min = 6, max = 25, message = "Please enter soundTId between 6 to 25 alphanumeric characters")
	private String soundTId;

	@Pattern(regexp = "Tone Tag|Worldline|CWD|Kiotel|", message = "Please pass Tone Tag, Worldline, CWD or Kiotel soundBoxProvider name.")
	private String soundBoxProvider;

	@Pattern(regexp = "Hindi|English|Bengali|Marathi|Gujarati|Malyalam|Punjabi|Tamil|Telugu|Kannada|", message = "Please pass Hindi,English,Marathi,Malyalam,Punjabi,Tamil,Telugu,Kannada or Gujarati soundBoxLanguage")
	private String soundBoxLanguage;

	public long getSoundBoxSubscriptionId() {
		return soundBoxSubscriptionId;
	}

	public void setSoundBoxSubscriptionId(long soundBoxSubscriptionId) {
		this.soundBoxSubscriptionId = soundBoxSubscriptionId;
	}

	public String getSoundTId() {
		return soundTId;
	}

	public void setSoundTId(String soundTId) {
		this.soundTId = soundTId;
	}

	public String getSoundBoxProvider() {
		return soundBoxProvider;
	}

	public void setSoundBoxProvider(String soundBoxProvider) {
		this.soundBoxProvider = soundBoxProvider;
	}

	public String getSoundBoxLanguage() {
		return soundBoxLanguage;
	}

	public void setSoundBoxLanguage(String soundBoxLanguage) {
		this.soundBoxLanguage = soundBoxLanguage;
	}

}
