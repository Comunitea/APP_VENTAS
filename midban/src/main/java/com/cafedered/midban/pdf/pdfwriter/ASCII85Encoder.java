/*******************************************************************************
 * MidBan is an Android App which allows to interact with OpenERP
 *     Copyright (C) 2014  CafedeRed
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.cafedered.midban.pdf.pdfwriter;

// Copyright (c) 2005, Luc Maisonobe
// All rights reserved.
//
// Redistribution and use in source and binary forms, with
// or without modification, are permitted provided that
// the following conditions are met:
//
//    Redistributions of source code must retain the
//    above copyright notice, this list of conditions and
//    the following disclaimer.
//    Redistributions in binary form must reproduce the
//    above copyright notice, this list of conditions and
//    the following disclaimer in the documentation
//    and/or other materials provided with the
//    distribution.
//    Neither the names of spaceroots.org, spaceroots.com
//    nor the names of their contributors may be used to
//    endorse or promote products derived from this
//    software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
// CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
// PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
// THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
// USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
// IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
// USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.


import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class encodes a binary stream into a text stream.
 * 
 * <p>
 * The ASCII85encoding is suitable when binary data needs to be transmitted or stored as text. It has been defined by
 * Adobe for the PostScript and PDF formats (see PDF Reference, section 3.3 Details of Filtered Streams).
 * </p>
 * 
 * <p>
 * The encoded stream is about 25% larger than the corresponding binary stream (32 binary bits are converted into 40
 * encoded bits, and there may be start/end of line markers).
 * </p>
 * 
 * @author Luc Maisonobe
 * @see ASCII85Decoder
 */
public class ASCII85Encoder extends FilterOutputStream
{

    /**
     * Create an encoder wrapping a sink of binary data.
     * <p>
     * Calling this constructor is equivalent to calling {@link #ASCII85Encoder(OutputStream,int,byte[],byte[])
     * ASCII85Encoder(<code>out</code>, <code>-1</code>, <code>null</code>, <code>null</code>)}.
     * </p>
     * 
     * @param out
     *            sink of binary data to filter
     */
    public ASCII85Encoder( OutputStream out )
    {
        super( out );
        this.lineLength = -1;
        this.c1 = -1;
        this.phase = 4;
    }

    /**
     * Create an encoder wrapping a sink of binary data.
     * <p>
     * The additional arguments allow to specify some text formatting
     * </p>
     * <p>
     * Note that specifying a negative number for <code>lineLength</code> is really equivalent to calling the one
     * argument {@link #ASCII85Encoder(OutputStream) constructor}.
     * </p>
     * <p>
     * If non-null start/end of line are used, they must contain only whitespace characters as other characters would
     * otherwise interfere with the decoding process on the other side of the channel. For safety, it is recommended to
     * stick to space (' ', 0x32) and horizontal tabulation ('\t', 0x9) characters for the start of line marker, and to
     * line feed ('\n', 0xa) and carriage return ('\r', 0xd) characters according to the platform convention for the end
     * of line marker.
     * </p>
     * 
     * @param out
     *            sink of binary data to filter
     * @param lineLength
     *            maximal length of a ligne (counting <code>sol</code> but not counting <code>eol</code>), if
     *            negative lines will not be split
     * @param sol
     *            start of line marker to use (mainly for indentation purposes), may be null
     * @param eol
     *            end of line marker to use, may be null only if <code>lineLength</code> is negative
     */
    public ASCII85Encoder( OutputStream out, int lineLength, byte[] sol, byte[] eol )
    {
        super( out );
        this.lineLength = lineLength;
        this.sol = sol;
        this.eol = eol;
        this.c1 = -1;
        this.phase = 4;
    }

    /**
     * Closes this output stream and releases any system resources associated with the stream.
     * 
     * @exception IOException
     *                if the underlying stream throws one
     */
    @Override
    public void close() throws IOException
    {

        if ( this.c1 >= 0 )
        {
            this.c4 += this.c5 / 85;
            this.c3 += this.c4 / 85;
            this.c2 += this.c3 / 85;
            this.c1 += this.c2 / 85;

            // output only the required number of bytes
            putByte( 33 + this.c1 );
            putByte( 33 + (this.c2 % 85) );
            if ( this.phase > 1 )
            {
                putByte( 33 + (this.c3 % 85) );
                if ( this.phase > 2 )
                {
                    putByte( 33 + (this.c4 % 85) );
                    if ( this.phase > 3 )
                    {
                        putByte( 33 + (this.c5 % 85) );
                    }
                }
            }

            // output the end marker
            putByte( '~' );
            putByte( '>' );

        }

        // end the last line properly
        if ( this.length != 0 )
        {
            this.out.write( this.eol, 0, this.eol.length );
        }

        // close the underlying stream
        this.out.close();

    }

    /**
     * Writes the specified byte to this output stream.
     * 
     * @param b
     *            byte to write (only the 8 low order bits are used)
     */
    @Override
    public void write( int b ) throws IOException
    {

        b = b & 0xff;

        switch( this.phase )
        {
        case 1:
            this.c3 += 9 * b;
            this.c4 += 6 * b;
            this.c5 += b;
            this.phase = 2;
            break;
        case 2:
            this.c4 += 3 * b;
            this.c5 += b;
            this.phase = 3;
            break;
        case 3:
            this.c5 += b;
            this.phase = 4;
            break;
        default:
            if ( this.c1 >= 0 )
            {
                // there was a preceding quantum, we now know it was not the last
                if ( (this.c1 == 0) && (this.c2 == 0) && (this.c3 == 0) && (this.c4 == 0) && (this.c5 == 0) )
                {
                    putByte( 'z' );
                }
                else
                {
                    this.c4 += this.c5 / 85;
                    this.c3 += this.c4 / 85;
                    this.c2 += this.c3 / 85;
                    this.c1 += this.c2 / 85;
                    putByte( 33 + this.c1 );
                    putByte( 33 + (this.c2 % 85) );
                    putByte( 33 + (this.c3 % 85) );
                    putByte( 33 + (this.c4 % 85) );
                    putByte( 33 + (this.c5 % 85) );
                }
            }
            this.c1 = 0;
            this.c2 = 27 * b;
            this.c3 = this.c2;
            this.c4 = 9 * b;
            this.c5 = b;
            this.phase = 1;
        }

    }

    /**
     * Put a byte in the underlying stream, inserting line breaks as needed.
     * 
     * @param b
     *            byte to put in the underlying stream (only the 8 low order bits are used)
     * @exception IOException
     *                if the underlying stream throws one
     */
    private void putByte( int b ) throws IOException
    {
        if ( this.lineLength >= 0 )
        {
            // split encoded lines if needed
            if ( (this.length == 0) && (this.sol != null) )
            {
                this.out.write( this.sol, 0, this.sol.length );
                this.length = this.sol.length;
            }
            this.out.write( b );
            if ( ++this.length >= this.lineLength )
            {
                this.out.write( this.eol, 0, this.eol.length );
                this.length = 0;
            }
        }
        else
        {
            this.out.write( b );
        }
    }

    /** Line length (not counting eol). */
    private final int		lineLength;

    /** Start of line marker (indentation). */
    private byte[]	sol;

    /** End Of Line marker. */
    private byte[]	eol;

    /** Coefficients of the 32-bits quantum in base 85. */
    private int		c1;
    private int		c2;
    private int		c3;
    private int		c4;
    private int		c5;

    /** Phase (between 1 and 4) of raw bytes. */
    private int		phase;

    /** Current length of the line being written. */
    private int		length;

}