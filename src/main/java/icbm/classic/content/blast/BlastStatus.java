package icbm.classic.content.blast;

import icbm.classic.lib.data.status.ActionStatus;

public class BlastStatus {

    public static final String TRANSLATION_PREFIX = "info.icbmclassic:blast";
    public static final String ERROR_TRANSLATION = TRANSLATION_PREFIX + ".error";

    // <editor-fold description="error status">
    public static ActionStatus UNKNOWN_ERROR = new ActionStatus().asError()
        .withRegName("blast.error.spawning.canceled").withTranslation(ERROR_TRANSLATION + ".unknown");

    public static ActionStatus EXPLOSIVE_EVENT_CANCELED = new ActionStatus().asError()
        .withRegName("blast.error.event.canceled").withTranslation(ERROR_TRANSLATION + ".event.canceled");

    public static ActionStatus ENTITY_SPAWN_CANCELED = new ActionStatus().asError()
        .withRegName("blast.error.spawning.canceled").withTranslation(ERROR_TRANSLATION + ".spawning.canceled");

    public static ActionStatus SETUP_ERROR = new ActionStatus().asError()
        .withRegName("blast.error.setup").withTranslation(ERROR_TRANSLATION + ".setup");
    // </editor-fold>

    // <editor-fold description="good status">
    public static ActionStatus TRIGGERED = new ActionStatus()
        .withRegName("blast.triggered.normal").withTranslation(TRANSLATION_PREFIX + ".triggered.normal");

    public static ActionStatus TRIGGERED_CLIENT = new ActionStatus()
        .withRegName("blast.triggered.client").withTranslation(TRANSLATION_PREFIX + ".triggered.client");

    public static ActionStatus TRIGGERED_THREADING = new ActionStatus()
        .withRegName("blast.triggered.threading").withTranslation(TRANSLATION_PREFIX + ".triggered.threading");

    public static ActionStatus TRIGGERED_DONE = new ActionStatus()
        .withRegName("blast.triggered.done").withTranslation(TRANSLATION_PREFIX + ".triggered.done");
    // </editor-fold>

}
