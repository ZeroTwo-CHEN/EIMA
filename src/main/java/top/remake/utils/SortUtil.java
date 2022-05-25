package top.remake.utils;

import top.remake.component.ThumbnailPanel;
import top.remake.entity.ImageFile;
import top.remake.entity.SortOrder;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @author ZeroTwo_CHEN
 */
public class SortUtil {
    private static final Collator COLLATOR_INSTANCE = Collator.getInstance(Locale.CHINA);

    private SortUtil() {
    }

    public static void sortThumbnailPanel(List<ThumbnailPanel> thumbnailPanels, String sortOrder) {
        switch (sortOrder) {
            case SortOrder.ASC_SORT_BY_NAME ->
                    thumbnailPanels.sort(((o1, o2) -> COLLATOR_INSTANCE.compare(o1.getImageFile().getFileName(), o2.getImageFile().getFileName())));
            case SortOrder.DESC_SORT_BY_NAME ->
                    thumbnailPanels.sort(((o1, o2) -> COLLATOR_INSTANCE.compare(o2.getImageFile().getFileName(), o1.getImageFile().getFileName())));
            case SortOrder.ASC_SORT_BY_TIME ->
                    thumbnailPanels.sort(Comparator.comparing(o -> o.getImageFile().getCreationTime()));
            case SortOrder.DESC_SORT_BY_TIME ->
                    thumbnailPanels.sort((o1, o2) -> o2.getImageFile().getCreationTime().compareTo(o1.getImageFile().getCreationTime()));
            case SortOrder.ASC_SORT_BY_SIZE ->
                    thumbnailPanels.sort(Comparator.comparingLong(o -> o.getImageFile().getSizeInBytes()));
            case SortOrder.DESC_SORT_BY_SIZE ->
                    thumbnailPanels.sort((o1, o2) -> Long.compare(o2.getImageFile().getSizeInBytes(), o1.getImageFile().getSizeInBytes()));
        }
    }

    public static void sortImageFile(List<ImageFile> imageFiles, String sortOrder) {
        switch (sortOrder) {
            case SortOrder.ASC_SORT_BY_NAME ->
                    imageFiles.sort((o1, o2) -> COLLATOR_INSTANCE.compare(o1.getFileName(), o2.getFileName()));
            case SortOrder.DESC_SORT_BY_NAME ->
                    imageFiles.sort((o1, o2) -> COLLATOR_INSTANCE.compare(o2.getFileName(), o1.getFileName()));
            case SortOrder.ASC_SORT_BY_TIME -> imageFiles.sort(Comparator.comparing(ImageFile::getCreationTime));
            case SortOrder.DESC_SORT_BY_TIME ->
                    imageFiles.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
            case SortOrder.ASC_SORT_BY_SIZE -> imageFiles.sort(Comparator.comparingLong(ImageFile::getSizeInBytes));
            case SortOrder.DESC_SORT_BY_SIZE ->
                    imageFiles.sort((o1, o2) -> Long.compare(o2.getSizeInBytes(), o1.getSizeInBytes()));
        }
    }
}
