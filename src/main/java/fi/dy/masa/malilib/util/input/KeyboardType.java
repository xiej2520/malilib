package fi.dy.masa.malilib.util.input;

import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum KeyboardType implements IConfigOptionListEntry
{
	QWERTY          ("qwerty",    "malilib.label.keyboard_type.qwerty"),
	AZERTY          ("azerty",    "malilib.label.keyboard_type.azerty"),
	;

	public static final ImmutableList<KeyboardType> VALUES = ImmutableList.copyOf(values());

	private final String configString;
	private final String translationKey;

	KeyboardType(String configString, String translationKey)
	{
		this.configString = configString;
		this.translationKey = translationKey;
	}

	@Override
	public String getStringValue()
	{
		return this.configString;
	}

	@Override
	public String getDisplayName()
	{
		return StringUtils.translate(this.translationKey);
	}

	@Override
	public IConfigOptionListEntry cycle(boolean forward)
	{
		int id = this.ordinal();

		if (forward)
		{
			if (++id >= values().length)
			{
				id = 0;
			}
		}
		else
		{
			if (--id < 0)
			{
				id = values().length - 1;
			}
		}

		return values()[id % values().length];
	}

	@Override
	public KeyboardType fromString(String name)
	{
		return fromStringStatic(name);
	}

	public static KeyboardType fromStringStatic(String name)
	{
		for (KeyboardType mode : KeyboardType.VALUES)
		{
			if (mode.configString.equalsIgnoreCase(name))
			{
				return mode;
			}
		}

		return KeyboardType.QWERTY;
	}
}
