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

package com.cubeia.backend.cashgame;


class CallbackHandler {
    boolean called = false;
    final Pointer<Object> responsePtr = new Pointer<Object>();

    void setResponse(Object response) {
        synchronized (responsePtr) {
            if (called) {
                throw new IllegalStateException("callback already invoked");
            }
            called = true;
            responsePtr.object = response;
            responsePtr.notify();
        }
    }

    Object getResponse(long timeout) throws InterruptedException {
        synchronized (responsePtr) {
            if (called) {
                return responsePtr.object;
            }

            responsePtr.wait(timeout);
            return responsePtr.object;
        }
    }

    class Pointer<T> {
        public volatile T object = null;
    }
}