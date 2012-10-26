/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.client;

import com.cubeia.firebase.clients.java.connector.text.AbstractClientPacketHandler;
import com.cubeia.firebase.clients.java.connector.text.IOContext;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.firebase.io.protocol.*;
import com.cubeia.games.poker.io.protocol.ProtocolObjectFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created on 2006-sep-19
 *
 * @author Fredrik Johansson
 *         <p/>
 *         $RCSFile: $
 *         $Revision: $
 *         $Author: $
 *         $Date: $
 */
public class ManualPacketHandler extends AbstractClientPacketHandler {

    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(ManualPacketHandler.class);

    @SuppressWarnings("unused")
    private IOContext context;

    /**
     * Handles the test game specific actions.
     * You need to inject an implementation before
     * using testgame packets, or they wont be handled!
     */
    private ManualGameHandler gameHandler;

    private StyxSerializer styxDecoder = new StyxSerializer(new ProtocolObjectFactory());

    public ManualPacketHandler(IOContext context) {
        this.context = context;
    }

    public ManualGameHandler getTestHandler() {
        return gameHandler;
    }

    /**
     * IOC injection.
     *
     * @param testHandler
     */
    public void setTestHandler(ManualGameHandler testHandler) {
        this.gameHandler = testHandler;
    }

    public void visit(GameTransportPacket packet) {
        ProtocolObject data;
        try {
            data = styxDecoder.unpack(ByteBuffer.wrap(packet.gamedata));
            if (data != null) {
                data.accept(gameHandler);
            }
        } catch (IOException e) {
            System.out.println("Can't create packet: " + packet);
            return;
        }
    }

    @Override
    public void visit(VersionPacket packet) {
    }

    @Override
    public void visit(GameVersionPacket packet) {
    }

    @Override
    public void visit(SystemMessagePacket packet) {
    }

    @Override
    public void visit(PingPacket packet) {
    }

    @Override
    public void visit(LoginResponsePacket packet) {
    }

    @Override
    public void visit(ForcedLogoutPacket packet) {
    }

    @Override
    public void visit(SeatInfoPacket packet) {
    }

    @Override
    public void visit(PlayerQueryResponsePacket packet) {
    }

    @Override
    public void visit(SystemInfoResponsePacket packet) {
    }

    @Override
    public void visit(JoinResponsePacket packet) {
    }

    @Override
    public void visit(WatchResponsePacket packet) {
    }

    @Override
    public void visit(UnwatchResponsePacket packet) {
    }

    @Override
    public void visit(LeaveResponsePacket packet) {
    }

    @Override
    public void visit(TableQueryResponsePacket packet) {
    }

    @Override
    public void visit(CreateTableResponsePacket packet) {
    }

    @Override
    public void visit(NotifyInvitedPacket packet) {
    }

    @Override
    public void visit(NotifyJoinPacket packet) {
    }

    @Override
    public void visit(NotifyLeavePacket packet) {
    }

    @Override
    public void visit(NotifyRegisteredPacket packet) {
    }

    @Override
    public void visit(NotifyWatchingPacket packet) {
    }

    @Override
    public void visit(KickPlayerPacket packet) {
    }

    @Override
    public void visit(ServiceTransportPacket packet) {
    }

    @Override
    public void visit(LocalServiceTransportPacket packet) {
    }

    @Override
    public void visit(MttTransportPacket packet) {
    }

    @Override
    public void visit(EncryptedTransportPacket packet) {
    }

    @Override
    public void visit(JoinChatChannelResponsePacket packet) {
    }

    @Override
    public void visit(NotifyChannelChatPacket packet) {
    }

    @Override
    public void visit(TableSnapshotPacket packet) {
    }

    @Override
    public void visit(TableUpdatePacket packet) {
    }

    @Override
    public void visit(TableRemovedPacket packet) {
    }

    @Override
    public void visit(TournamentSnapshotPacket packet) {
    }

    @Override
    public void visit(TournamentUpdatePacket packet) {
    }

    @Override
    public void visit(TournamentRemovedPacket packet) {
    }

    @Override
    public void visit(TableSnapshotListPacket packet) {
    }

    @Override
    public void visit(TableUpdateListPacket packet) {
    }

    @Override
    public void visit(TournamentSnapshotListPacket packet) {
    }

    @Override
    public void visit(TournamentUpdateListPacket packet) {
    }

    @Override
    public void visit(FilteredJoinTableResponsePacket packet) {
    }

    @Override
    public void visit(FilteredJoinCancelResponsePacket packet) {
    }

    @Override
    public void visit(FilteredJoinTableAvailablePacket packet) {
    }

    @Override
    public void visit(ProbePacket packet) {
    }

    @Override
    public void visit(MttRegisterResponsePacket packet) {
    }

    @Override
    public void visit(MttUnregisterResponsePacket packet) {
    }

    @Override
    public void visit(MttSeatedPacket packet) {
    }

    @Override
    public void visit(MttPickedUpPacket packet) {
    }

    @Override
    public void visit(NotifySeatedPacket packet) {
    }

}

