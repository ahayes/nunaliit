package ca.carleton.gcrc.olkit.multimedia.converter.threshold;

import ca.carleton.gcrc.olkit.multimedia.converter.MultimediaConversionThreshold;

public class DefaultThresholdVideo implements MultimediaConversionThreshold {

	protected static final long DEFAULT_MAX_BITRATE = 250000;
	protected static final String DEFAULT_REQUIRED_VIDEO_ENCODING = "h264";
	protected static final String DEFAULT_REQUIRED_AUDIO_ENCODING = "aac";
	protected static final int DEFAULT_MAX_DIMENSION = 500;

	@Override
	public boolean isConversionRequired(String videoFormat, Long videoRate, String audioFormat, Long audioRate,
										Long imageWidth, Long imageHeight, Long fileSizeMb) {

		boolean isConversionRequired = false;

		if (videoRate == null
				|| isResizeRequired(imageWidth, imageHeight)
				|| videoRate > DEFAULT_MAX_BITRATE
				|| !DEFAULT_REQUIRED_VIDEO_ENCODING.equalsIgnoreCase(videoFormat)
				|| !DEFAULT_REQUIRED_AUDIO_ENCODING.equalsIgnoreCase(audioFormat)) {
			isConversionRequired = true;
		}

		return isConversionRequired;
	}

	@Override
	public boolean isResizeRequired(Long imageWidth, Long imageHeight) {
		return imageWidth == null || imageWidth > DEFAULT_MAX_DIMENSION || imageHeight > DEFAULT_MAX_DIMENSION;
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}