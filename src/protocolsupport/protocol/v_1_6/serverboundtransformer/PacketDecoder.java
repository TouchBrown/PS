package protocolsupport.protocol.v_1_6.serverboundtransformer;

import java.io.IOException;
import java.util.List;

import protocolsupport.protocol.DataStorage;
import protocolsupport.protocol.PacketDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import net.minecraft.server.v1_8_R1.EnumProtocol;
import net.minecraft.server.v1_8_R1.NetworkManager;
import net.minecraft.server.v1_8_R1.Packet;

public class PacketDecoder extends ByteToMessageDecoder {

	@SuppressWarnings("unchecked")
	private static final AttributeKey<EnumProtocol> currentStateAttrKey = NetworkManager.c;

	private static final PacketTransformer[] transformers = new PacketTransformer[] {
		new HandshakePacketTransformer(),
		new PlayPacketTransformer(),
		new StatusPacketTransformer(),
		new LoginPacketTransformer()
	};

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> packets) throws Exception {
		if (bytebuf.readableBytes() == 0) {
			return;
		}
		Channel channel = ctx.channel();
		bytebuf.markReaderIndex();
		try {
			EnumProtocol currentProtocol = channel.attr(currentStateAttrKey).get();
			int packetId = bytebuf.readUnsignedByte();
			Packet[] transformedPackets = transformers[currentProtocol.ordinal()].tranform(
				channel,
				packetId,
				new PacketDataSerializer(bytebuf, DataStorage.getVersion(channel.remoteAddress()))
			);
			if (transformedPackets != null) {
				for (Packet transformedPacket : transformedPackets) {
					packets.add(transformedPacket);
				}
				return;
			} else {
				throw new IOException("Can't deserialize unknown packet "+packetId);
			}
		} catch (IndexOutOfBoundsException ex) {
		}
		bytebuf.resetReaderIndex();
	}

}