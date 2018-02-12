/*
 * Copyright (C) 1999-2004 By Ugo Chirico
 *
 * This is free software; you can redistribute it and/or
 * modify it under the terms of the Affero GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero GNU General Public License for more details.
 *
 * You should have received a copy of the Affero GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package com.ugos.jiprolog.extensions.io;

import com.ugos.jiprolog.engine.*;

import java.io.*;
import java.util.*;

public class Dir2 extends JIPXCall
{
    public final boolean unify(final JIPCons params, Hashtable<JIPVariable, JIPVariable> varsTbl)
    {
        // get first parameter
       JIPTerm path = params.getNth(1).getValue();

       if(path == null)
       {
    	   throw new JIPInstantiationException(1);
       }
       else if(!(path instanceof JIPAtom))
       {
    	   throw new JIPTypeException(JIPTypeException.ATOM, path);
       }

       File file = new File(((JIPAtom)path).getName());
       String files[] = file.list();
       JIPList fileList = JIPList.create(JIPAtom.create("."), null);
       fileList = JIPList.create(JIPAtom.create(".."), fileList);

       if(files != null)
       {
           for(int i = 0; i < files.length; i++)
           {
               fileList = JIPList.create(JIPAtom.create(files[i]), fileList);
           }
       }

       fileList = fileList.reverse();

       return params.getNth(2).unify(fileList, varsTbl);
    }

    public boolean hasMoreChoicePoints()
    {
        return false;
    }
}