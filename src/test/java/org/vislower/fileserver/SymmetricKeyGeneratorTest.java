package org.vislower.fileserver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SymmetricKeyGeneratorTest {

    @Test
    void checkIfCreateAESKeyReturnsNonNullKey(){
        assertNotNull(SymmetricKeyGenerator.createAESKey());
    }

}