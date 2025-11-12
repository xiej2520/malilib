package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Used to Copy Files via the GUI
 *
 * @param file
 * @param navigator
 * @param feedback
 */
public final class FileCopier implements IStringConsumerFeedback
{
	private final Path file;
	@Nullable private final IDirectoryNavigator navigator;
	private final boolean feedback;

	public FileCopier(Path file, @Nullable IDirectoryNavigator navigator, boolean feedback)
	{
		this.file = file;
		this.navigator = navigator;
		this.feedback = feedback;
	}

	public Path file() { return this.file; }

	@Nullable
	public IDirectoryNavigator navigator() { return this.navigator; }

	public boolean feedback() { return this.feedback; }

	@Override
	public boolean setString(String string)
	{
		if (string.isEmpty() || this.file() == null)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.invalid_file_or_directory");
			MaLiLib.logger.warn("FileCopier: Failed to copy file; File is invalid/empty.");
			return false;
		}

		Path dir = this.file().getParent();
		Path newFile = dir.resolve(FileUtils.generateSafeFileName(string)).normalize();

		if (this.file().getFileName().equals(newFile.getFileName()))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_copy_file.destination_exists", this.file().toAbsolutePath(), newFile.toAbsolutePath());
			MaLiLib.logger.warn("FileCopier: Failed to copy file '{}'; Destination is the same.", this.file().toAbsolutePath());
			return true;        // Closes Dialog box
		}

		if (!Files.exists(this.file()))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_does_not_exist",
			                                    this.file().toAbsolutePath());
			MaLiLib.logger.warn("FileCopier: Failed to copy file '{}'; Source does not exist.", this.file().toAbsolutePath());
			return false;
		}

		if (Files.exists(newFile))
		{
			// Hold Shift to Overwrite destination file
			if (GuiBase.isShiftDown())
			{
				try
				{
					Files.delete(newFile);
				}
				catch (Exception err)
				{
					InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_delete_file", this.file().toAbsolutePath());
					MaLiLib.logger.debug("FileCopier: Failed to delete file '{}'; {}", this.file().toAbsolutePath(), err.getLocalizedMessage());
					return false;
				}
			}
			else
			{
				InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_copy_file.destination_exists",
				                                    this.file().toAbsolutePath(), newFile.toAbsolutePath());
				MaLiLib.logger.debug("FileCopier: Failed to copy file '{}'; Destination file exists.", this.file().toAbsolutePath());
				return false;
			}
		}

		try
		{
			Files.copy(this.file(), newFile);
		}
		catch (Exception err)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_copy_file.exception",
			                                    this.file().toAbsolutePath(), newFile.toAbsolutePath(), err.getLocalizedMessage());
			MaLiLib.logger.debug("FileCopier: Exception copying file '{}'; {}", this.file().toAbsolutePath(), err.getLocalizedMessage());
			return false;
		}

		if (feedback())
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.file_copied", this.file().getFileName(), newFile.getFileName());
		}

		MaLiLib.logger.debug("FileCopier: Copied file '{}' -> '{}'", this.file().toAbsolutePath(), newFile.toAbsolutePath());
		return true;
	}
}
