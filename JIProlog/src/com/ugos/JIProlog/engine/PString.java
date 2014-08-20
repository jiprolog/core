/*
 * 23/04/2014
 *
 * Copyright (C) 1999-2014 Ugo Chirico
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

package com.ugos.JIProlog.engine;

//import java.io.Serializable;
import java.util.Hashtable;

final class PString extends List //implements Serializable
{
    final static long serialVersionUID = 300000007L;
    
    private String m_strString;
    //private int    m_nHashValue;
    
    public PString(final PrologObject head, final ConsCell tail)
    {
        this(new List(head, tail));
    }
    
    // ottimizzazione
    private PString(final PrologObject head, final ConsCell tail, String string)
    {
        super(new List(head, tail));
        m_strString = string;
    }
    
    private PString()
    {
        super(List.NIL);
        m_strString = "";
    }
    
    public PString(final List string)
    {
        super(string);
        
        PrologObject tail = string;
        PrologObject head = ((ConsCell)tail).getHead();
        
//      System.out.println(head);
        
        m_strString = "";
        
        while (head != null)
        {
            if (head instanceof Variable)
            {
                if (((Variable)head).isBounded())
                {
                    head = ((Variable)head).getObject();
                }
            }
                                    
            if (head instanceof Expression)
            {
                Expression ascii = (Expression)head;
                
                if (!ascii.isInteger())
                {
                    throw new JIPParameterTypeException();
                }
                
                int nAscii = (int)ascii.getValue();
                
                if(nAscii < 0 || nAscii > 255)
                    throw new JIPParameterTypeException();
                
                m_strString += String.valueOf((char)nAscii);
            }
            else
            {
                throw new JIPParameterTypeException();
            }

            if (tail instanceof Variable)
            {
                tail = ((Variable)tail).getObject();;
            }
            
//          System.out.println("1" + tail.getClass());
            
            tail = ((ConsCell)tail).getTail();
            
            if (tail instanceof Variable)
            {
                tail = ((Variable)tail).getObject();;
            }
            
            if (tail == null)
            {
                head = null;
            }
            else
            {
                head = ((ConsCell)tail).getHead();
            }
        }
        
//      m_nHashValue = m_strString.hashCode();
    }

    public PString(final String strString)
    {
        super(getList(strString));
        m_strString   = strString;
        //m_nHashValue  = m_strString.hashCode();
        
        //m_list = getList(strString);
    }

    public final PrologObject copy(final Hashtable varTable)
    {
        return new PString(getString());
//        if(m_head != null)
//        {
//            return new PString(m_head.copy(varTable),
//                              (ConsCell)((m_tail == null) ? null : m_tail.copy(varTable)),
//                              m_strString);
//        }
//        else
//        {
//            return new PString();
//        }
        //return new PString((List)super.copy(varTable));
    }
    
    public final boolean _unify(final PrologObject obj, final Hashtable table)
    {
        if (obj instanceof List)
        {
            return super._unify(obj, table);
        }
        else if(obj instanceof Variable)
        {
            return ((Variable)obj)._unify(this, table);
        }
        else
        {
            return false;
        }
    }
    
    private static final List getList(final String string)
    {
        List retList = null;
        
        for(int i = string.length() - 1; i >= 0; i--)
        {
            retList = new List(Expression.createNumber(string.charAt(i)), retList);
        }
        
        return retList;
    }
        
    public final String getString()
    {
        return m_strString;
    }

    protected final boolean lessThen(final PrologObject obj)
    {
        if(obj instanceof PString)
            return m_strString.compareTo( ((PString)obj).m_strString) < 0;
        if(obj instanceof List)
            return super.lessThen(obj);
        else if(obj instanceof Atom)
            return false;
        else if(obj instanceof Expression)
            return false;
        else if(obj instanceof Variable)
            if(((Variable)obj).isBounded())
                return lessThen(((Variable)obj).getObject());
            else
                return false;
        
        return true;
    }
}
