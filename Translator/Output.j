.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 ldc 10
 ldc 5
 idiv 
 ldc 2
 ldc 56
 ldc 3
 ldc 2
 ldc 3
 iadd 
 ldc 34
 ldc 35
 invokestatic Output/print(I)V
L1:
 invokestatic Output/read()I
 istore 0
L2:
 iload 0
 ldc 10
 if_icmpgt L3
 goto L4
L3:
 invokestatic Output/read()I
 istore 1
L4:
 iload 0
 ldc 12
 invokestatic Output/print(I)V
L5:
 invokestatic Output/read()I
 istore 1
L6:
 iload 1
 ldc 2
 if_icmpeq L7
 goto L8
L7:
 iload 0
 ldc 1
 iadd 
 istore 0
 goto L6
L8:
L9:
 iload 0
 invokestatic Output/print(I)V
L10:
 invokestatic Output/read()I
 istore 1
L11:
 ldc 1236
 istore 1
L12:
 iload 1
 invokestatic Output/print(I)V
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

