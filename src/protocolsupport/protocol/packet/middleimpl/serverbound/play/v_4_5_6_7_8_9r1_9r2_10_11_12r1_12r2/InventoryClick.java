package protocolsupport.protocol.packet.middleimpl.serverbound.play.v_4_5_6_7_8_9r1_9r2_10_11_12r1_12r2;

import io.netty.buffer.ByteBuf;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.middle.serverbound.play.MiddleInventoryClick;
import protocolsupport.protocol.serializer.ItemStackSerializer;
import protocolsupport.protocol.utils.types.WindowType;
import protocolsupport.zplatform.itemstack.ItemStackWrapper;

public class InventoryClick extends MiddleInventoryClick {

	@Override
	public void readFromClientData(ByteBuf clientdata) {
		windowId = clientdata.readUnsignedByte();
		slot = clientdata.readShort();
		ProtocolVersion version = connection.getVersion();
		if (version.isBefore(ProtocolVersion.MINECRAFT_1_9) && (cache.getWindowCache().getOpenedWindow() == WindowType.BREWING)) {
			if (slot > 3) {
				slot++;
			}
		} else if (version.isBefore(ProtocolVersion.MINECRAFT_1_8) && (cache.getWindowCache().getOpenedWindow() == WindowType.ENCHANT)) {
			if (slot > 0) {
				slot++;
			}
		}
		button = clientdata.readUnsignedByte();
		actionNumber = clientdata.readShort();
		mode = clientdata.readUnsignedByte();
		itemstack = ItemStackSerializer.readItemStack(clientdata, version, cache.getAttributesCache().getLocale(), true);
		if (button == 0 && mode == 1) {
			itemstack = ItemStackWrapper.NULL;
		}
	}

}
