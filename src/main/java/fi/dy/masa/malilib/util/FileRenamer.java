package fi.dy.masa.malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

/**
 * Used to Rename / Move files via the GUI
 *
 * @param file
 * @param navigator
 * @param feedback
 */
public final class FileRenamer implements IStringConsumerFeedback
{
	private final Path file;
	@Nullable private final IDirectoryNavigator navigator;
	private final boolean feedback;

	public FileRenamer(Path file, @Nullable IDirectoryNavigator navigator, boolean feedback)
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
			MaLiLib.logger.debug("FileRenamer: Failed to rename file; File is invalid/empty.");
			return false;
		}

		Path dir = this.file().getParent();
		Path newFile = dir.resolve(FileUtils.generateSafeFileName(string)).normalize();

//	    MaLiLib.logger.error("RENAME: [{}] --> [{}] (dir: '{}')", this.file.toAbsolutePath(), newFile.toAbsolutePath(), dir.toAbsolutePath());

		if (this.file().getFileName().equals(newFile.getFileName()))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_rename.same_name", newFile.toAbsolutePath());
			MaLiLib.logger.debug("FileRenamer: Failed to rename file '{}'; Destination is the same.", this.file().toAbsolutePath());
			return true;        // Closes Dialog box
		}

		if (!Files.exists(this.file()))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_does_not_exist",
			                                    this.file().toAbsolutePath());
			MaLiLib.logger.debug("FileRenamer: Failed to rename file '{}'; Source does not exist.", this.file().toAbsolutePath());
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
					MaLiLib.logger.debug("FileRenamer: Failed to delete file '{}'; {}", this.file().toAbsolutePath(), err.getLocalizedMessage());
					return false;
				}
			}
			else
			{
				InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_rename_file.exists",
				                                    this.file().toAbsolutePath(), newFile.toAbsolutePath());
				MaLiLib.logger.debug("FileRenamer: Failed to rename file '{}'; Destination file exists.", this.file().toAbsolutePath());
				return false;
			}
		}

		try
		{
			Files.move(this.file(), newFile);
		}
		catch (Exception err)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_rename_file.exception",
			                                    this.file().toAbsolutePath(), newFile.toAbsolutePath(), err.getLocalizedMessage());
			MaLiLib.logger.debug("FileRenamer: Exception renaming file '{}'; {}", this.file().toAbsolutePath(), err.getLocalizedMessage());
			return false;
		}

		if (feedback())
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.file_or_directory_renamed", this.file().getFileName(), newFile.getFileName());
		}

		MaLiLib.logger.debug("FileRenamer: Renamed file '{}' -> '{}'", this.file().toAbsolutePath(), newFile.toAbsolutePath());
		return true;
	}
}
