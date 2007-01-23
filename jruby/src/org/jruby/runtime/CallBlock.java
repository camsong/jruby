/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Ola Bini <ola@ologix.com>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.runtime;

import org.jruby.RubyModule;
import org.jruby.exceptions.JumpException;
import org.jruby.internal.runtime.methods.AbstractCallable;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * A Block implemented using a Java-based BlockCallback implementation
 * rather than with an ICallable. For lightweight block logic within
 * Java code.
 */
public class CallBlock extends Block {
    private Arity arity;
    private BlockCallback callback;
    private IRubyObject self;
    private RubyModule imClass;
    private ThreadContext tc;

    public CallBlock(IRubyObject self, RubyModule imClass, Arity arity, BlockCallback callback, ThreadContext ctx) {
        super(null,
                new CallMethod(callback),
                self,
                ctx.getCurrentFrame(),
                ctx.peekCRef(),
                new Scope(),
                ctx.getRubyClass(),
                Iter.ITER_PRE,
                ctx.getCurrentScope());
        this.arity = arity;
        this.callback = callback;
        this.self = self;
        this.imClass = imClass;
        this.tc = ctx;
    }

    public IRubyObject call(ThreadContext context, IRubyObject[] args, IRubyObject replacementSelf) {
        return callback.call(context, args, replacementSelf);
    }

    public Block cloneBlock() {
        return new CallBlock(self,imClass,arity,callback,tc);
    }

    public Arity arity() {
        return arity;
    }

    public static class CallMethod extends AbstractCallable {
        private BlockCallback callback;
        public CallMethod(BlockCallback callback) {
            this.callback = callback;
        }

        public IRubyObject call(ThreadContext context, IRubyObject receiver, IRubyObject[] args) {
            return callback.call(context, args,receiver);
        }

        public ICallable dup() {
            return new CallMethod(callback);
        }
    }    
}
