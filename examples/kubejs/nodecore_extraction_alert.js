// Reference script — copy to kubejs/server_scripts/ in your modpack.
// Requires KubeJS + Node Core. Event class:
//   com.mrwizard94.nodecore.event.ExtractionAlertEvent

ForgeEvents.onEvent('com.mrwizard94.nodecore.event.ExtractionAlertEvent', event => {
    const player = event.getPlayer()
    const pos = event.getPos()
    const node = event.getNode()

    // Example: suppress alerts when the placer is in creative mode
    if (player != null && player.isCreative()) {
        event.cancel()
        return
    }

    // Example: replace the default server-wide message
    const playerName = player != null ? player.getGameProfile().getName() : 'Unknown'
    event.setAlertMessage(
        Text.translate(
            'nodecore.message.extraction_alert',
            playerName,
            pos.getX(),
            pos.getZ()
        ).red().bold()
    )

    // Example: include node type in a fully custom message instead
    // event.setAlertMessage(
    //     Text.red('[' + node.getType().getId() + '] Drill at ' + pos.getX() + ', ' + pos.getZ())
    // )
})